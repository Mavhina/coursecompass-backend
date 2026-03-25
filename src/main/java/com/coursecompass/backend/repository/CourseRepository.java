package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // ✅ All courses for a university (ALL faculties)
    List<Course> findByUniversityId(Long universityId);

    // ✅ Optional: filter by faculty
    List<Course> findByUniversityIdAndFacultyId(Long universityId, Long facultyId);
}
