package com.coursecompass.backend.service;

import com.coursecompass.backend.dto.MessageDTO;
import com.coursecompass.backend.domain.Message;
import com.coursecompass.backend.repository.MessageRepository;
import com.coursecompass.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository    userRepository;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    public Message saveMessage(Long senderId, Long receiverId,
                               String text, String senderRole) {
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setText(text);
        msg.setSenderRole(senderRole);
        return messageRepository.save(msg);
    }

    public List<MessageDTO> getConversation(Long tutorId, Long studentId) {
        return messageRepository.findConversation(tutorId, studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MessageDTO> getGroupConversation(Long groupId) {
        return messageRepository.findGroupConversation(groupId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MessageDTO> getBroadcastMessages(Long tutorId) {
        return messageRepository.findBroadcastMessages(tutorId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Long> getTutorIdsForStudent(Long studentId) {
        return messageRepository.findTutorIdsThatMessagedStudent(studentId);
    }

    public List<Long> getStudentIdsForTutor(Long tutorId) {
        return messageRepository.findStudentIdsThatMessagedTutor(tutorId);
    }

    public MessageDTO getLatestMessage(Long tutorId, Long studentId) {
        return messageRepository.findLatestMessage(tutorId, studentId)
                .map(this::toDTO).orElse(null);
    }

    public List<MessageDTO> getBroadcastMessagesFromTutors(List<Long> tutorIds) {
        if (tutorIds == null || tutorIds.isEmpty()) return List.of();
        return messageRepository.findBroadcastMessagesFromTutors(tutorIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Unread counts ─────────────────────────────────────────────────────

    // For student: count unread from a specific tutor
    public long countUnreadFromTutor(Long tutorId, Long studentId) {
        return messageRepository.countUnreadFromTutor(tutorId, studentId);
    }

    // For tutor: count unread from a specific student
    public long countUnreadFromStudent(Long studentId, Long tutorId) {
        return messageRepository.countUnreadFromStudent(studentId, tutorId);
    }

    // ── Mark as read ──────────────────────────────────────────────────────

    // Student opens chat → mark tutor's messages as read
    @Transactional
    public void markTutorMessagesAsRead(Long tutorId, Long studentId) {
        messageRepository.markTutorMessagesAsRead(tutorId, studentId);
    }

    // Tutor opens chat → mark student's messages as read
    @Transactional
    public void markStudentMessagesAsRead(Long studentId, Long tutorId) {
        messageRepository.markStudentMessagesAsRead(studentId, tutorId);
    }

    // Convert Message → MessageDTO
    public MessageDTO toDTO(Message msg) {
        MessageDTO dto = new MessageDTO();
        dto.setId(msg.getId());
        dto.setSenderId(msg.getSenderId());
        dto.setReceiverId(msg.getReceiverId());
        dto.setText(msg.getText());
        dto.setSender(msg.getSenderRole().startsWith("tutor") ? "tutor" : "student");
        dto.setSentAt(msg.getSentAt());
        dto.setTime(msg.getSentAt().format(TIME_FMT));
        dto.setStatus(msg.isRead() ? "read" : "delivered");
        return dto;
    }
}