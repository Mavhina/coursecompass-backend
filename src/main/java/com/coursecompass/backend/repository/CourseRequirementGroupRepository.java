package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.CourseRequirementGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRequirementGroupRepository extends JpaRepository<CourseRequirementGroup, Long> {
    List<CourseRequirementGroup> findByCourseIdOrderBySortOrderAsc(Long courseId);
}
