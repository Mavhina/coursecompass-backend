package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.ApiResponse;
import com.coursecompass.backend.dto.MessageDTO;
import com.coursecompass.backend.dto.ChatMessageRequest;
import com.coursecompass.backend.domain.Message;
import com.coursecompass.backend.repository.UserRepository;
import com.coursecompass.backend.service.GroupMessageReadService;
import com.coursecompass.backend.service.MessageService;
import com.coursecompass.backend.service.StudentGroupService;
import com.coursecompass.backend.dto.StudentGroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentMessageController {

    private final MessageService          messageService;
    private final GroupMessageReadService groupReadService;
    private final SimpMessagingTemplate   messagingTemplate;
    private final UserRepository          userRepository;
    private final StudentGroupService     groupService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) return user;
        throw new RuntimeException("Not authenticated");
    }

    // ── Get all chats for student ─────────────────────────────────────────
    @GetMapping("/chats")
    public ApiResponse<List<Map<String, Object>>> getStudentChats() {
        User student = getCurrentUser();
        List<Long> tutorIds = messageService.getTutorIdsForStudent(student.getId());

        List<Map<String, Object>> chats = new ArrayList<>();
        for (Long tutorId : tutorIds) {
            userRepository.findById(tutorId).ifPresent(tutor -> {
                MessageDTO latest = messageService.getLatestMessage(tutorId, student.getId());
                long unread = messageService.countUnreadFromTutor(tutorId, student.getId());

                Map<String, Object> chat = new HashMap<>();
                chat.put("tutorId",       tutorId);
                chat.put("tutorName",     tutor.getFullName());
                chat.put("tutorEmail",    tutor.getEmail());
                chat.put("lastMessage",   latest != null ? latest.getText() : "");
                chat.put("lastTime",      latest != null ? latest.getTime() : "");
                chat.put("lastTimestamp", latest != null && latest.getSentAt() != null
                        ? latest.getSentAt().toString() : "");
                chat.put("unreadCount",   unread);
                chats.add(chat);
            });
        }
        return ApiResponse.success(chats);
    }

    // ── Get groups the student is in ──────────────────────────────────────
    @GetMapping("/groups")
    public ApiResponse<List<StudentGroupDTO>> getStudentGroups() {
        User student = getCurrentUser();
        return ApiResponse.success(groupService.getGroupsForStudent(student.getId()));
    }

    // ── Get 1:1 message history with a tutor ──────────────────────────────
    @GetMapping("/messages/{tutorId}")
    public ApiResponse<List<MessageDTO>> getHistory(@PathVariable Long tutorId) {
        User student = getCurrentUser();
        return ApiResponse.success(messageService.getConversation(tutorId, student.getId()));
    }

    // ── Get group message history ─────────────────────────────────────────
    @GetMapping("/messages/group/{groupId}")
    public ApiResponse<List<MessageDTO>> getGroupHistory(@PathVariable Long groupId) {
        return ApiResponse.success(messageService.getGroupConversation(groupId));
    }

    // ── Get broadcast messages ────────────────────────────────────────────
    @GetMapping("/messages/broadcast")
    public ApiResponse<List<MessageDTO>> getBroadcasts() {
        User student = getCurrentUser();
        List<Long> tutorIds = messageService.getTutorIdsForStudent(student.getId());
        return ApiResponse.success(messageService.getBroadcastMessagesFromTutors(tutorIds));
    }

    // ── Unread counts: 1:1 chats + group chats combined ──────────────────
    @GetMapping("/messages/unread-counts")
    public ApiResponse<Map<String, Object>> getUnreadCounts() {
        User student = getCurrentUser();

        // 1:1 unread per tutorId
        List<Long> tutorIds = messageService.getTutorIdsForStudent(student.getId());
        Map<String, Long> chatCounts = new HashMap<>();
        for (Long tutorId : tutorIds) {
            long count = messageService.countUnreadFromTutor(tutorId, student.getId());
            if (count > 0) chatCounts.put(String.valueOf(tutorId), count);
        }

        // Group unread per groupId
        Map<String, Long> groupCounts = groupReadService.getUnreadCountsPerGroup(student.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("chats",  chatCounts);
        result.put("groups", groupCounts);
        return ApiResponse.success(result);
    }

    // ── Mark 1:1 tutor messages as read ───────────────────────────────────
    @PatchMapping("/messages/{tutorId}/read")
    public ApiResponse<Void> markChatAsRead(@PathVariable Long tutorId) {
        User student = getCurrentUser();
        messageService.markTutorMessagesAsRead(tutorId, student.getId());
        return ApiResponse.success(null);
    }

    // ── Mark group messages as read ───────────────────────────────────────
    @PatchMapping("/messages/group/{groupId}/read")
    public ApiResponse<Void> markGroupAsRead(@PathVariable Long groupId) {
        User student = getCurrentUser();
        groupReadService.markGroupAsRead(student.getId(), groupId);
        return ApiResponse.success(null);
    }

    // ── WebSocket: student → tutor ────────────────────────────────────────
    @MessageMapping("/student-chat/{tutorId}")
    public void handleStudentMessage(@DestinationVariable Long tutorId,
                                     ChatMessageRequest request, Principal principal) {
        if (principal == null) return;
        User student = (User) ((Authentication) principal).getPrincipal();
        Message saved = messageService.saveMessage(student.getId(), tutorId, request.getText(), "student");
        MessageDTO dto = messageService.toDTO(saved);
        messagingTemplate.convertAndSend("/topic/messages/" + student.getId(), dto);
        messagingTemplate.convertAndSend("/topic/messages/student/" + student.getId(), dto);
    }

    // ── WebSocket: student sends to group ─────────────────────────────────
    @MessageMapping("/student-group-chat/{groupId}")
    public void handleStudentGroupMessage(@DestinationVariable Long groupId,
                                          ChatMessageRequest request, Principal principal) {
        if (principal == null) return;
        User student = (User) ((Authentication) principal).getPrincipal();
        Message saved = messageService.saveMessage(student.getId(), groupId, request.getText(), "tutor-group");
        MessageDTO dto = messageService.toDTO(saved);

        // Create unread records for all other group members
        groupReadService.createUnreadRecordsForGroupMessage(saved.getId(), groupId, student.getId());

        messagingTemplate.convertAndSend("/topic/group-messages/" + groupId, dto);
    }
}