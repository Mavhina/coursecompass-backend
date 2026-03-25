package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Session;
import com.coursecompass.backend.domain.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findAllByTutorIdOrderByDateAscStartTimeAsc(Long tutorId);

    @Query("SELECT s FROM Session s WHERE s.tutorId = :tutorId AND s.status = :status ORDER BY s.date ASC, s.startTime ASC")
    List<Session> findByTutorIdAndStatus(@Param("tutorId") Long tutorId, @Param("status") SessionStatus status);

    @Query("SELECT s FROM Session s WHERE s.tutorId = :tutorId AND YEAR(s.date) = :year AND MONTH(s.date) = :month ORDER BY s.date ASC, s.startTime ASC")
    List<Session> findByTutorIdAndMonth(@Param("tutorId") Long tutorId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT s FROM Session s WHERE s.tutorId = :tutorId AND s.status = :status AND YEAR(s.date) = :year AND MONTH(s.date) = :month ORDER BY s.date ASC, s.startTime ASC")
    List<Session> findByTutorIdAndStatusAndMonth(@Param("tutorId") Long tutorId, @Param("status") SessionStatus status, @Param("year") int year, @Param("month") int month);

    Optional<Session> findByIdAndTutorId(Long id, Long tutorId);
}