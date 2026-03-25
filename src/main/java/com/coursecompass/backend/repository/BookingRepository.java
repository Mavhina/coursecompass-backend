package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Booking;
import com.coursecompass.backend.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByStudentIdAndTutorId(Long studentId, Long tutorId);

    @Query("SELECT b FROM Booking b WHERE b.tutorId = :tutorId")
    List<Booking> findByTutorId(@Param("tutorId") Long tutorId);

    @Query("SELECT b FROM Booking b WHERE b.tutorId = :tutorId AND b.status = com.coursecompass.backend.domain.BookingStatus.ACCEPTED")
    List<Booking> findAcceptedByTutorId(@Param("tutorId") Long tutorId);

    @Query("SELECT b FROM Booking b WHERE b.studentId = :studentId AND b.tutorId = :tutorId " +
            "AND b.status IN (com.coursecompass.backend.domain.BookingStatus.PENDING, " +
            "com.coursecompass.backend.domain.BookingStatus.ACCEPTED)")
    Optional<Booking> findActiveBooking(@Param("studentId") Long studentId, @Param("tutorId") Long tutorId);

    boolean existsByStudentIdAndTutorIdAndStatusIn(Long studentId, Long tutorId, List<BookingStatus> statuses);

    @Query(value = "SELECT b.*, t.name as tutor_name, t.image as tutor_image, t.subject as tutor_subject " +
            "FROM bookings b " +
            "JOIN tutors t ON b.tutor_id = t.id " +
            "WHERE b.student_id = :studentId " +
            "ORDER BY b.created_at DESC", nativeQuery = true)
    List<Booking> findAllByStudentIdWithTutorInfo(@Param("studentId") Long studentId);

    // ── Fixed: uses full_name + email ──
    @Query(value = "SELECT b.*, u.full_name as student_name, u.email as student_email " +
            "FROM bookings b " +
            "JOIN users u ON b.student_id = u.id " +
            "WHERE b.tutor_id = :tutorId " +
            "ORDER BY b.created_at DESC", nativeQuery = true)
    List<Booking> findAllByTutorIdWithStudentInfo(@Param("tutorId") Long tutorId);

    List<Booking> findAllByTutorIdAndStatusOrderByCreatedAtDesc(Long tutorId, BookingStatus status);

    long countByTutorIdAndStatus(Long tutorId, BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.tutorId = :tutorId AND b.status = com.coursecompass.backend.domain.BookingStatus.ACCEPTED")
    Long countDistinctStudentsByTutorId(@Param("tutorId") Long tutorId);

    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.tutorId = :tutorId AND b.status = com.coursecompass.backend.domain.BookingStatus.ACCEPTED " +
            "AND DATE(b.createdAt) >= :startDate AND DATE(b.createdAt) <= :endDate")
    Long countDistinctStudentsByTutorIdAndDateRange(
            @Param("tutorId") Long tutorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(b.price), 0) FROM Booking b " +
            "WHERE b.tutorId = :tutorId AND b.status = com.coursecompass.backend.domain.BookingStatus.ACCEPTED")
    BigDecimal sumEarningsByTutorId(@Param("tutorId") Long tutorId);

    @Query("SELECT COALESCE(SUM(b.price), 0) FROM Booking b " +
            "WHERE b.tutorId = :tutorId AND b.status = com.coursecompass.backend.domain.BookingStatus.ACCEPTED " +
            "AND YEAR(b.createdAt) = :year AND MONTH(b.createdAt) = :month")
    BigDecimal sumMonthlyEarningsByTutorId(
            @Param("tutorId") Long tutorId,
            @Param("year") int year,
            @Param("month") int month);

    @Query(value = """
        SELECT
            TO_CHAR(b.created_at, 'Mon') AS month,
            TO_CHAR(b.created_at, 'YYYY-MM') AS monthKey,
            COALESCE(SUM(b.price), 0) AS earnings
        FROM bookings b
        WHERE b.tutor_id = :tutorId
          AND b.status = 1
          AND b.created_at >= NOW() - INTERVAL '6 months'
        GROUP BY TO_CHAR(b.created_at, 'YYYY-MM'), TO_CHAR(b.created_at, 'Mon')
        ORDER BY monthKey ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyEarningsLast6Months(@Param("tutorId") Long tutorId);

// Replace both findAcceptedByTutorIdWithStudentInfo and findAcceptedByTutorIdAndMonth
// to return List<Object[]> instead of List<Booking>

    @Query(value = """
SELECT
    b.id, b.student_id, b.tutor_id, b.business_id, b.price,
    b.payment_status, b.payment_due_date, b.created_at,
    CASE
        WHEN b.student_id IS NOT NULL THEN u.full_name
        ELSE CONCAT('(Guest) ', es.name)
    END as student_name,
    CASE
        WHEN b.student_id IS NOT NULL THEN u.email
        ELSE es.email
    END as student_email,
    b.external_student_id
FROM bookings b
LEFT JOIN users u ON b.student_id = u.id
LEFT JOIN external_students es ON b.external_student_id = es.id
WHERE b.tutor_id = :tutorId AND b.status = 1
ORDER BY b.created_at DESC
""", nativeQuery = true)
    List<Object[]> findAcceptedByTutorIdWithStudentInfo(@Param("tutorId") Long tutorId);

    @Query(value = """
SELECT
    b.id, b.student_id, b.tutor_id, b.business_id, b.price,
    b.payment_status, b.payment_due_date, b.created_at,
    CASE
        WHEN b.student_id IS NOT NULL THEN u.full_name
        ELSE CONCAT('(Guest) ', es.name)
    END as student_name,
    CASE
        WHEN b.student_id IS NOT NULL THEN u.email
        ELSE es.email
    END as student_email,
    b.external_student_id
FROM bookings b
LEFT JOIN users u ON b.student_id = u.id
LEFT JOIN external_students es ON b.external_student_id = es.id
WHERE b.tutor_id = :tutorId AND b.status = 1
AND EXTRACT(YEAR FROM b.created_at) = :year
AND EXTRACT(MONTH FROM b.created_at) = :month
ORDER BY b.created_at DESC
""", nativeQuery = true)
    List<Object[]> findAcceptedByTutorIdAndMonth(
            @Param("tutorId") Long tutorId,
            @Param("year") int year,
            @Param("month") int month);
}