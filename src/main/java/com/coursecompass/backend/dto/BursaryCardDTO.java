package com.coursecompass.backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BursaryCardDTO {
    private Long id;
    private String slug;
    private String name;
    private String provider;
    private String type;
    private String logoUrl;
    private String status;
    private LocalDate closingDate;
    private String fundingType;
    private List<String> studyLevels;
    private List<String> institutionTypes;
    private List<String> fields;
    private String summary;

    private RequirementsPreviewDTO requirementsPreview;
}
