package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryField;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BursaryFieldRepository extends JpaRepository<BursaryField, Long> {
    List<BursaryField> findByBursaryId(Long bursaryId);
}
