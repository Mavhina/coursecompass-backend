package com.coursecompass.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorCardDTO {
    private Long user_id;
    private String name;
    private List<String> subjects;
    private String grades;
    private Integer pricePerMonth;
    private Double rating;
    private Integer reviewsCount;
    private String mode;
    private String location;
    private String imageUrl;
    private Long businessId;
    private Integer price;
}
