package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.Course;
import com.coursecompass.backend.domain.CourseUniversityRequirement;
import com.coursecompass.backend.domain.University;
import com.coursecompass.backend.repository.CourseUniversityRequirementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseUniversityRequirementService {

    private final CourseUniversityRequirementRepository requirementRepository;

    public CourseUniversityRequirementService(CourseUniversityRequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public CourseUniversityRequirement saveRequirement(CourseUniversityRequirement requirement) {
        return requirementRepository.save(requirement);
    }

    public List<CourseUniversityRequirement> getAllRequirements() {
        return requirementRepository.findAll();
    }

    public List<Course> getCoursesQualified(int studentAps, University university) {
        return requirementRepository.findByUniversity(university).stream()
                .filter(r -> studentAps >= r.getMinimumAps())
                .map(CourseUniversityRequirement::getCourse)
                .collect(Collectors.toList());
    }

    public Optional<Integer> getApsForCourseAtUniversity(Course course, University university) {
        return requirementRepository.findByCourseAndUniversity(course, university)
                .map(CourseUniversityRequirement::getMinimumAps);
    }
}
