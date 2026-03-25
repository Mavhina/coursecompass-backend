package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.ChatMessage;
import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.ChatDtos;
import com.coursecompass.backend.repository.ChatMessageRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChatMessageService {

    private final ChatMessageRepository repo;

    public ChatMessageService(ChatMessageRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ChatDtos.MessageResponse create(User user, ChatDtos.CreateMessageRequest req) {
        if (req.getRoom() == null || req.getRoom().isBlank()) {
            throw new IllegalArgumentException("room is required");
        }
        if (req.getMessage() == null || req.getMessage().isBlank()) {
            throw new IllegalArgumentException("message is required");
        }

        ChatMessage saved = repo.save(ChatMessage.builder()
                .room(req.getRoom().trim().toLowerCase())
                .message(req.getMessage().trim())
                .user(user)
                .isVisible(true)
                .build());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ChatDtos.ListResponse list(String room, int page, int size) {
        String normalizedRoom = (room == null ? "community" : room.trim().toLowerCase());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ChatMessage> p = repo.findByRoomAndIsVisibleTrueOrderByCreatedAtAsc(normalizedRoom, pageable);

        return ChatDtos.ListResponse.builder()
                .room(normalizedRoom)
                .items(p.getContent().stream().map(this::toResponse).toList())
                .page(ChatDtos.PageDTO.builder()
                        .number(p.getNumber())
                        .size(p.getSize())
                        .totalElements(p.getTotalElements())
                        .totalPages(p.getTotalPages())
                        .build())
                .build();
    }

    @Transactional
    public ChatDtos.DeleteResponse softDelete(Long id, User requester) {
        ChatMessage msg = repo.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));

        boolean isOwner = msg.getUser().getId().equals(requester.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(requester.getRole()); // adjust if role is enum

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not allowed to delete this message");
        }

        msg.setVisible(false);
        msg.setDeletedAt(LocalDateTime.now());
        repo.save(msg);

        return ChatDtos.DeleteResponse.builder()
                .success(true)
                .id(msg.getId())
                .isVisible(msg.isVisible())
                .deletedAt(msg.getDeletedAt())
                .build();
    }

    private ChatDtos.MessageResponse toResponse(ChatMessage m) {
        return ChatDtos.MessageResponse.builder()
                .id(m.getId())
                .room(m.getRoom())
                .message(m.getMessage())
                .isVisible(m.isVisible())
                .createdAt(m.getCreatedAt())
                .user(ChatDtos.UserDTO.builder()
                        .id(m.getUser().getId())
                        .username(m.getUser().getEmail())   // if you don’t have username, use email OR add username column
                        .fullName(m.getUser().getFullName())
                        .build())
                .build();
    }
}
