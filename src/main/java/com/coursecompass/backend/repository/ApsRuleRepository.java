package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.ApsRule;
import com.coursecompass.backend.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApsRuleRepository extends JpaRepository<ApsRule, Long> {
    Optional<ApsRule> findByUniversity(University university);
}
