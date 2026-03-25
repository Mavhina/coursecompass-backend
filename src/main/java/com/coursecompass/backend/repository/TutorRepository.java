package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Tutor;
import com.coursecompass.backend.domain.TutorBusiness;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    // ✅ load tutor + user + tutorSubjects + subject in one go
    @EntityGraph(attributePaths = {"user", "tutorSubjects", "tutorSubjects.subject"})
    List<Tutor> findAll();

    // NEW: Find tutor by user ID
    @Query("SELECT t FROM Tutor t WHERE t.user.id = :userId")
    Optional<Tutor> findByUserId(@Param("userId") Long userId);
}
