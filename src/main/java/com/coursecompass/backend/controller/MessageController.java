package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.ApiResponse;
import com.coursecompass.backend.dto.ChatMessageRequest;
import com.coursecompass.backend.dto.MessageDTO;
import com.coursecompass.backend.domain.Message;
import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.repository.UserRepository;
import com.coursecompass.backend.service.MessageService;
import com.coursecompass.backend.service.GroupMessageReadService;
import com.coursecompass.backend.service.TutorStudentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService          messageService;
    private final GroupMessageReadService groupReadService;
    private final SimpMessagingTemplate   messagingTemplate;
    private final UserRepository          userRepository;
    private final TutorStudentsService    studentsService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) return user;
        throw new RuntimeException("Not authenticated");
    }

    private User getPrincipalUser(Principal principal) {
        return (User) ((Authentication) principal).getPrincipal();
    }

    // ── 1:1 WebSocket: tutor → student ───────────────────────────────────
    @MessageMapping("/chat/{studentId}")
    public void handleMessage(@DestinationVariable Long studentId,
                              ChatMessageRequest request, Principal principal) {
        if (principal == null) return;
        User tutor = getPrincipalUser(principal);
        Message saved = messageService.saveMessage(tutor.getId(), studentId, request.getText(), "tutor");
        MessageDTO dto = messageService.toDTO(saved);
        messagingTemplate.convertAndSend("/topic/messages/" + studentId, dto);
        messagingTemplate.convertAndSend("/topic/messages/student/" + studentId, dto);
    }

    // ── Group WebSocket: tutor → group ────────────────────────────────────
    @MessageMapping("/group-chat/{groupId}")
    public void handleGroupMessage(@DestinationVariable Long groupId,
                                   ChatMessageRequest request, Principal principal) {
        if (principal == null) return;
        User tutor = getPrincipalUser(principal);
        Message saved = messageService.saveMessage(tutor.getId(), groupId, request.getText(), "tutor-group");
        MessageDTO dto = messageService.toDTO(saved);

        // Create unread records for every group member except the sender
        groupReadService.createUnreadRecordsForGroupMessage(saved.getId(), groupId, tutor.getId());

        messagingTemplate.convertAndSend("/topic/group-messages/" + groupId, dto);
    }

    // ── Broadcast WebSocket: tutor → all students ─────────────────────────
    @MessageMapping("/broadcast")
    public void handleBroadcast(ChatMessageRequest request, Principal principal) {
        if (principal == null) return;
        User tutor = getPrincipalUser(principal);
        Message saved = messageService.saveMessage(tutor.getId(), tutor.getId(), request.getText(), "broadcast");
        MessageDTO dto = messageService.toDTO(saved);
        messagingTemplate.convertAndSend("/topic/broadcast/" + tutor.getId(), dto);
    }

    // ── REST: 1:1 history ─────────────────────────────────────────────────
    @GetMapping("/tutor/messages/{studentId}")
    public ApiResponse<List<MessageDTO>> getHistory(@PathVariable Long studentId) {
        User tutor = getCurrentUser();
        return ApiResponse.success(messageService.getConversation(tutor.getId(), studentId));
    }

    // ── REST: group history ───────────────────────────────────────────────
    @GetMapping("/tutor/messages/group/{groupId}")
    public ApiResponse<List<MessageDTO>> getGroupHistory(@PathVariable Long groupId) {
        return ApiResponse.success(messageService.getGroupConversation(groupId));
    }

    // ── REST: broadcast history ───────────────────────────────────────────
    @GetMapping("/tutor/messages/broadcast")
    public ApiResponse<List<MessageDTO>> getBroadcastHistory() {
        User tutor = getCurrentUser();
        return ApiResponse.success(messageService.getBroadcastMessages(tutor.getId()));
    }

    // ── REST: tutor unread counts per student ─────────────────────────────
    @GetMapping("/tutor/messages/unread-counts")
    public ApiResponse<Map<String, Long>> getTutorUnreadCounts() {
        User tutor = getCurrentUser();
        List<Long> studentIds = messageService.getStudentIdsForTutor(tutor.getId());
        Map<String, Long> counts = new HashMap<>();
        for (Long studentId : studentIds) {
            long count = messageService.countUnreadFromStudent(studentId, tutor.getId());
            if (count > 0) counts.put(String.valueOf(studentId), count);
        }
        return ApiResponse.success(counts);
    }

    // ── REST: tutor marks student messages as read ────────────────────────
    @PatchMapping("/tutor/messages/{studentId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long studentId) {
        User tutor = getCurrentUser();
        messageService.markStudentMessagesAsRead(studentId, tutor.getId());
        return ApiResponse.success(null);
    }
}