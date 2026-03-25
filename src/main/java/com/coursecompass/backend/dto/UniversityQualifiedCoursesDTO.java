package com.coursecompass.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniversityQualifiedCoursesDTO {
    private String universityName;
    private List<QualifiedCourseDTO> courses;
}
