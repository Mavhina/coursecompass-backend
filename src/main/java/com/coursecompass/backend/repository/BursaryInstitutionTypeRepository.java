package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryInstitutionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryInstitutionTypeRepository extends JpaRepository<BursaryInstitutionType, Long> {

    List<BursaryInstitutionType> findByBursaryId(Long bursaryId);
}
