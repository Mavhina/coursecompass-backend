package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.service.CourseService;
import com.coursecompass.backend.service.CourseUniversityRequirementService;
import com.coursecompass.backend.service.SubjectService;
import com.coursecompass.backend.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CourseService courseService;
    private final UniversityService universityService;
    private final CourseUniversityRequirementService requirementService;
    private final SubjectService subjectService;

    public AdminController(CourseService courseService,
                           UniversityService universityService,
                           CourseUniversityRequirementService requirementService, SubjectService subjectService) {
        this.courseService = courseService;
        this.universityService = universityService;
        this.requirementService = requirementService;
        this.subjectService = subjectService;
    }

    @PostMapping("/universities")
    public ResponseEntity<University> addUniversity(@RequestBody University university) {
        University saved = universityService.saveUniversity(university);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/requirements")
    public ResponseEntity<CourseUniversityRequirement> addRequirement(
            @RequestBody CourseUniversityRequirement req) {
        CourseUniversityRequirement saved = requirementService.saveRequirement(req);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> addSubject(@RequestBody Subject subject) {
        Subject saved = subjectService.createSubject(subject);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }
}
