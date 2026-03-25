package com.coursecompass.backend.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FaqDTO {
    private String q;
    private String a;
}
