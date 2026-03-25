package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.FeeFundApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeFundApplicationRepository extends JpaRepository<FeeFundApplication, Long> {
    Page<FeeFundApplication> findByUserId(Long userId, Pageable pageable);

}
