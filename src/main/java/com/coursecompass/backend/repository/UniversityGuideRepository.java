package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.University;
import com.coursecompass.backend.domain.UniversityGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityGuideRepository extends JpaRepository<UniversityGuide, Long> {
    Optional<UniversityGuide> findByUniversity(University university);
    Optional<UniversityGuide> findByUniversity_Id(Long universityId);
}
