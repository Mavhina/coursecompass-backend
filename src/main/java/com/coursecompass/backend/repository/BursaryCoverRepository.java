package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryCover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryCoverRepository extends JpaRepository<BursaryCover, Long> {
    List<BursaryCover> findByBursaryId(Long bursaryId);
}
