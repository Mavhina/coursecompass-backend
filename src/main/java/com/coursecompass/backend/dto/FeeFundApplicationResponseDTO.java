package com.coursecompass.backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeFundApplicationResponseDTO {
    private Long id;
    private Long userId;
    private Long universityId;
    private String studentNumber;
    private String programme;
    private Integer amountRequested;
    private LocalDate deadlineDate;
    private String motivation;
    private String status;
    private List<DocumentDTO> documents;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DocumentDTO {
        private Long id;
        private String originalFileName;
        private String contentType;
        private Long fileSize;
        private String storageProvider;
        private String storageKey;
        private String publicUrl;
    }
}
