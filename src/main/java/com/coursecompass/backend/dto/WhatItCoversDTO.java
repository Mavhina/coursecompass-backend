package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class WhatItCoversDTO {
    private List<String> covers;
    private String notes;
}
