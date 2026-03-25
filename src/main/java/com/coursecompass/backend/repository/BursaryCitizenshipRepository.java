package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryCitizenship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryCitizenshipRepository extends JpaRepository<BursaryCitizenship, Long> {

    List<BursaryCitizenship> findByBursaryId(Long bursaryId);
}
