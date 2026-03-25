package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserSnapshotDTO {
    private Integer aps;
    private String citizenship;
    private Long householdIncome;
    private String level;
    private String institutionType;
    private List<String> fieldsInterested;
}
