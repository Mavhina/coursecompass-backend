package com.coursecompass.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private String name;
    private Integer years;
    private Integer minAps;
    private String minApsRule;
    private String faculty;
    private Boolean inDemand;
    private String description;

}
