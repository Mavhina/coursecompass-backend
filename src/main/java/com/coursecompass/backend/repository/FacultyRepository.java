package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByUniversityIdAndNameIgnoreCase(Long universityId, String name);
    List<Faculty> findByUniversityIdOrderByName(Long universityId);

}
