package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.QualifiedCourseDTO;
import com.coursecompass.backend.dto.UniversityQualifiedCoursesDTO;
import com.coursecompass.backend.repository.StudentApsRepository;
import com.coursecompass.backend.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final CourseUniversityRequirementService requirementService;
    private final UniversityService universityService;
    private final CourseService courseService;
    private final ApsCalculationService apsCalculationService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final StudentApsRepository studentApsRepo;

    public StudentController(
            CourseUniversityRequirementService requirementService,
            UniversityService universityService,
            CourseService courseService,
            ApsCalculationService apsCalculationService,
            StudentService studentService,
            SubjectService subjectService, StudentApsRepository studentApsRepo) {

        this.requirementService = requirementService;
        this.universityService = universityService;
        this.courseService = courseService;
        this.apsCalculationService = apsCalculationService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.studentApsRepo = studentApsRepo;
    }

    // -------------------- Calculate APS for Logged-in Student --------------------
    @GetMapping("/my-aps/{universityId}")
    public int getMyAps(
            @PathVariable Long universityId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Student student = studentService.getByUser(user);
        University university = universityService.getUniversityById(universityId);

        return apsCalculationService.calculateAps(student, university);
    }

    // -------------------- Submit ALL Marks (APS Test Flow) --------------------
    @PostMapping("/marks")
    @Transactional
    public List<StudentSubjectResult> submitMarks(
            @RequestBody List<StudentSubjectResult> marks,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Student student = studentService.getByUser(user);

        // Optional: clear old marks before saving new ones
        studentService.deleteResultsByStudent(student);

        for (StudentSubjectResult r : marks) {
            r.setStudent(student);
            r.setSubject(
                    subjectService.getSubjectById(
                            r.getSubject().getId()
                    )
            );
            studentService.saveResult(r);
        }

        return studentService.getResultsByStudent(student);
    }

    // -------------------- Get Student Marks --------------------
    @GetMapping("/marks")
    public List<StudentSubjectResult> getMarks(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Student student = studentService.getByUser(user);

        return studentService.getResultsByStudent(student);
    }

    // -------------------- Update a single mark --------------------
    @PutMapping("/marks/{resultId}")
    @Transactional
    public StudentSubjectResult updateMark(
            @PathVariable Long resultId,
            @RequestBody StudentSubjectResult updatedResult,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Student student = studentService.getByUser(user);

        // Get the existing result
        StudentSubjectResult existingResult = studentService.getResultById(resultId);

        // Make sure the result belongs to this student
        if (!existingResult.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("You can only update your own marks");
        }

        // Update the percentage
        existingResult.setPercentage(updatedResult.getPercentage());

        // Optionally, you could also update the subject if needed
        // existingResult.setSubject(subjectService.getSubjectById(updatedResult.getSubject().getId()));

        return studentService.saveResult(existingResult);
    }

}
