package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryInstitutionRepository extends JpaRepository<BursaryInstitution, Long> {

    List<BursaryInstitution> findByBursaryId(Long bursaryId);
}
