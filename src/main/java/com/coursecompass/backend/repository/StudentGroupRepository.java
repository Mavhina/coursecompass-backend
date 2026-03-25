package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    List<StudentGroup> findAllByTutorId(Long tutorId);
    Optional<StudentGroup> findByIdAndTutorId(Long id, Long tutorId);

    // Add this method to your existing StudentGroupRepository.java

    @Query("""
    SELECT DISTINCT g FROM StudentGroup g
    JOIN g.members m
    WHERE m.studentId = :studentId
       OR m.externalStudentId = :studentId
""")
    List<StudentGroup> findGroupsByStudentId(@Param("studentId") Long studentId);
}