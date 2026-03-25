package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BursaryRequiredDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BursaryRequiredDocumentRepository extends JpaRepository<BursaryRequiredDocument, Long> {

    List<BursaryRequiredDocument> findByBursaryId(Long bursaryId);
}
