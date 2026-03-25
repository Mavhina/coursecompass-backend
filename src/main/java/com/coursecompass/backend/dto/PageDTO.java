package com.coursecompass.backend.dto;

import lombok.*;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PageDTO {
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
}
