package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.University;
import com.coursecompass.backend.dto.UniversityNameDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    @Query("select new com.coursecompass.backend.dto.UniversityNameDTO(u.id, u.name) from University u")
    List<UniversityNameDTO> findAllUniversityNames();
}
