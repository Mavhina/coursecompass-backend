package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.BookingStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BookingStatusHistory entity
 */
@Repository
public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Long> {

    /**
     * Find all status history for a specific booking
     */
    List<BookingStatusHistory> findAllByBookingIdOrderByChangedAtDesc(Long bookingId);

    /**
     * Find recent status changes by a user
     */
    List<BookingStatusHistory> findAllByChangedByOrderByChangedAtDesc(Long changedBy);
}