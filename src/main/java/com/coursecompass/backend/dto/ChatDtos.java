package com.coursecompass.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ChatDtos {

    @Getter @Setter
    public static class CreateMessageRequest {
        private String room;     // "community"
        private String message;  // "Hi everyone!"
    }

    @Getter @Setter @Builder
    public static class UserDTO {
        private Long id;
        private String username;
        private String fullName;
    }

    @Getter @Setter @Builder
    public static class MessageResponse {
        private Long id;
        private String room;
        private String message;
        private boolean isVisible;
        private LocalDateTime createdAt;
        private UserDTO user;
    }

    @Getter @Setter @Builder
    public static class DeleteResponse {
        private boolean success;
        private Long id;
        private boolean isVisible;
        private LocalDateTime deletedAt;
    }

    @Getter @Setter @Builder
    public static class PageDTO {
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;
    }

    @Getter @Setter @Builder
    public static class ListResponse {
        private String room;
        private java.util.List<MessageResponse> items;
        private PageDTO page;
    }
}
