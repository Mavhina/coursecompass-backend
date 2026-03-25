package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.ApsScale;
import com.coursecompass.backend.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApsScaleRepository extends JpaRepository<ApsScale, Long> {
    List<ApsScale> findByUniversity(University university);
}
