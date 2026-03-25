package com.coursecompass.backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeaturedBursaryDTO {
    private Long id;
    private String slug;
    private String name;
    private String provider;
    private String type;
    private String logoUrl;
    private String status;
    private LocalDate closingDate;
    private String summary;
    private List<String> covers;
    private EligibilitySnapshotDTO eligibilitySnapshot;
    private String applyUrl;
}

