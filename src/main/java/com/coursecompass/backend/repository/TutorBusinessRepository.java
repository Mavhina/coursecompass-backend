package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.TutorBusiness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TutorBusinessRepository extends JpaRepository<TutorBusiness, Long> {
    Optional<TutorBusiness> findByOwner_Id(Long ownerUserId);
    // Returns single business (if unique per owner)

    // NEW: Returns all businesses for a tutor
    List<TutorBusiness> findAllByOwner_Id(Long ownerId);
}
