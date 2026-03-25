package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryApplicationStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryApplicationStepRepository extends JpaRepository<BursaryApplicationStep, Long> {

    List<BursaryApplicationStep> findByBursaryIdOrderByStepOrderAsc(Long bursaryId);
}
