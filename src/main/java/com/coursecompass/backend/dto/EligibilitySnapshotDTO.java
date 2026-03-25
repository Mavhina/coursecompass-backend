package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EligibilitySnapshotDTO {
    private List<String> citizenship;
    private Integer incomeMax;
    private List<String> institutions;
    private List<String> levels;
}

