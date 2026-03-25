package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.TutorBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorBookingRepository extends JpaRepository<TutorBooking, Long> {
    Optional<TutorBooking> findByStudentUser_IdAndBusiness_Id(Long studentUserId, Long businessId);
}
