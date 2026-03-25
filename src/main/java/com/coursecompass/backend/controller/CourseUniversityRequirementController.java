package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.Course;
import com.coursecompass.backend.domain.CourseUniversityRequirement;
import com.coursecompass.backend.domain.University;
import com.coursecompass.backend.service.CourseUniversityRequirementService;
import com.coursecompass.backend.service.CourseService;
import com.coursecompass.backend.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requirements")
public class CourseUniversityRequirementController {

    private final CourseUniversityRequirementService requirementService;
    private final CourseService courseService;
    private final UniversityService universityService;

    public CourseUniversityRequirementController(CourseUniversityRequirementService requirementService,
                                                 CourseService courseService,
                                                 UniversityService universityService) {
        this.requirementService = requirementService;
        this.courseService = courseService;
        this.universityService = universityService;
    }

    @PostMapping
    public ResponseEntity<CourseUniversityRequirement> addRequirement(@RequestBody CourseUniversityRequirement requirement) {
        return ResponseEntity.ok(requirementService.saveRequirement(requirement));
    }

    @GetMapping
    public ResponseEntity<List<CourseUniversityRequirement>> getAllRequirements() {
        return ResponseEntity.ok(requirementService.getAllRequirements());
    }

    // Get all courses a student qualifies for at a university
    @GetMapping("/qualified")
    public ResponseEntity<List<Course>> getQualifiedCourses(@RequestParam int aps,
                                                            @RequestParam Long universityId) {
        University university = universityService.getUniversityById(universityId);
        if (university == null) return ResponseEntity.notFound().build();

        List<Course> courses = requirementService.getCoursesQualified(aps, university);
        return ResponseEntity.ok(courses);
    }
}
