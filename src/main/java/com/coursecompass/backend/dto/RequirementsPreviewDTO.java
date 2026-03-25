package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RequirementsPreviewDTO {
    private Integer apsMin;
    private List<String> citizenship;
    private Integer incomeMax;
    private Boolean bonded;
}

