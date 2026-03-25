package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.CourseResponseDTO;
import com.coursecompass.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{universityId}/faculties/{facultyId}/courses")
    public List<CourseResponseDTO> getCourses(
            @PathVariable Long universityId,
            @PathVariable Long facultyId
    ) {
        return courseService.getCoursesByUniversityAndFaculty(universityId, facultyId);
    }
}
