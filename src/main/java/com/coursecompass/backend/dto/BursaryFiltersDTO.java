package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BursaryFiltersDTO {
    private List<String> statuses;
    private List<String> fundingTypes;
    private List<String> studyLevels;
    private List<String> institutionTypes;
    private List<String> fields;
}
