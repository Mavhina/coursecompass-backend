package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Bursary;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BursaryRepository extends JpaRepository<Bursary, Long>, JpaSpecificationExecutor<Bursary> {
    Optional<Bursary> findBySlug(String slug);
}
