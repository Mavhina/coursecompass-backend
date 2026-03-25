package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.ExternalStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalStudentRepository extends JpaRepository<ExternalStudent, Long> {}