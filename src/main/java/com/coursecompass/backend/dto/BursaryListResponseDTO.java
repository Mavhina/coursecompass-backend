package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BursaryListResponseDTO {
    private FeaturedBursaryDTO featured;
    private List<BursaryCardDTO> items;
    private FeaturedBursaryDTO featuredSecondary;
    private PageDTO page;
}

