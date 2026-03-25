package com.coursecompass.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualifiedCourseDTO {
    private Long id;
    private String name;
    private String description;
    private boolean inDemand;
    private String vanity;
}
