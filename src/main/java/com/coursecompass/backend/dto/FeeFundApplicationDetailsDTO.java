package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeFundApplicationDetailsDTO {

    private Long id;

    private UniversityDTO university;

    private String programme;
    private String studentNumber;
    private Integer amountRequested;
    private String deadlineDate;

    private String motivation;

    private String status;
    private String adminNotes;
    private Boolean requestedMoreInfo;

    private String createdAt;
    private String updatedAt;

    private List<DocumentDTO> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniversityDTO {
        private Long id;
        private String name;
        private String location;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentDTO {
        private Long id;
        private String originalFileName;
        private String contentType;
        private Long fileSize;
        private String publicUrl;
    }
}
