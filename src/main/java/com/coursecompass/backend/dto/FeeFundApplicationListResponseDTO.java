package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeFundApplicationListResponseDTO {

    private List<ItemDTO> items;
    private PageDTO page;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private Long id;
        private UniversityDTO university;
        private String programme;
        private String studentNumber;
        private Integer amountRequested;
        private String deadlineDate;
        private String status;
        private String createdAt;
        private String updatedAt;
        private Integer documentsCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniversityDTO {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageDTO {
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
