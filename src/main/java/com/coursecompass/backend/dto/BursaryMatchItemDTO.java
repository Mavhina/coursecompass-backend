package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BursaryMatchItemDTO {
    private Long bursaryId;
    private String name;
    private String match; // ELIGIBLE/MAYBE/NOT_ELIGIBLE
    private List<String> reasons;
}
