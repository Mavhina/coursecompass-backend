package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.CourseUniversityRequirement;
import com.coursecompass.backend.domain.Course;
import com.coursecompass.backend.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseUniversityRequirementRepository extends JpaRepository<CourseUniversityRequirement, Long> {
    List<CourseUniversityRequirement> findByCourse(Course course);
    List<CourseUniversityRequirement> findByUniversity(University university);
    Optional<CourseUniversityRequirement> findByCourseAndUniversity(Course course, University university);
}
