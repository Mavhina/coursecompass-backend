package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private CourseDTO course;
    private List<Object> requirements;
    // requirement objects will be either:
    // {subject, minPercentage} OR {anyOf: [ ... ]}
}
