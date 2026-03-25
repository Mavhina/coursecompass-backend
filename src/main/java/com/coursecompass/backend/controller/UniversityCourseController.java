package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.CourseResponseDTO;
import com.coursecompass.backend.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
public class UniversityCourseController {

    private final CourseService courseService;

    public UniversityCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ✅ Returns ALL courses in the university (all faculties)
    // GET /api/universities/1/courses
    @GetMapping("/{universityId}/courses")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByUniversity(
            @PathVariable Long universityId,
            @RequestParam(required = false) Long facultyId
    ) {
        if (facultyId != null) {
            return ResponseEntity.ok(
                    courseService.getCoursesByUniversityAndFaculty(universityId, facultyId)
            );
        }

        return ResponseEntity.ok(courseService.getCoursesByUniversity(universityId));
    }
}
