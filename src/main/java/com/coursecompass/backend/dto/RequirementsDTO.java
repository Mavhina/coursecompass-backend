package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RequirementsDTO {
    private List<String> citizenship;
    private Integer apsMin;
    private Long incomeMax;

    private List<String> academic;
    private List<String> financial;
    private List<String> other;

    private Boolean bonded;
}
