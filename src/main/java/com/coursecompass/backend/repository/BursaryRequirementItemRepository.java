package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryRequirementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryRequirementItemRepository extends JpaRepository<BursaryRequirementItem, Long> {

    List<BursaryRequirementItem> findByBursaryId(Long bursaryId);
}
