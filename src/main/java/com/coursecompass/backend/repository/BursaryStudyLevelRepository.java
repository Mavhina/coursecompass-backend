package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryStudyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryStudyLevelRepository extends JpaRepository<BursaryStudyLevel, Long> {
    List<BursaryStudyLevel> findByBursaryId(Long bursaryId);
}
