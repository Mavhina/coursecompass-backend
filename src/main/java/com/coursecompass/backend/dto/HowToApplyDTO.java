package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class HowToApplyDTO {
    private String method;  // ONLINE/EMAIL/WALK_IN
    private List<String> steps;
    private String applyUrl;
}
