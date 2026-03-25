package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryFaq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryFaqRepository extends JpaRepository<BursaryFaq, Long> {

    List<BursaryFaq> findByBursaryId(Long bursaryId);
}
