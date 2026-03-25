package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.Booking;
import com.coursecompass.backend.domain.BookingStatus;
import com.coursecompass.backend.domain.BookingStatusHistory;
import com.coursecompass.backend.dto.BookingResponse;
import com.coursecompass.backend.dto.CreateBookingRequest;
import com.coursecompass.backend.dto.TutorResponseRequest;
import com.coursecompass.backend.repository.BookingRepository;
import com.coursecompass.backend.repository.BookingStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing tutor bookings
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository historyRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          BookingStatusHistoryRepository historyRepository) {
        this.bookingRepository = bookingRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * Create a new booking
     */
    @Transactional
    public Booking createBooking(Long studentId, CreateBookingRequest request) {
        // Check if student already has an active booking with this tutor
        Optional<Booking> existingBooking = bookingRepository.findActiveBooking(
                studentId, request.getTutorId()
        );

        if (existingBooking.isPresent()) {
            Booking booking = existingBooking.get();
            if (booking.getStatus() == BookingStatus.PENDING) {
                throw new IllegalStateException("You already have a pending booking request with this tutor");
            } else if (booking.getStatus() == BookingStatus.ACCEPTED) {
                throw new IllegalStateException("You already have an active booking with this tutor");
            }
        }

        // Create new booking
        Booking booking = new Booking();
        booking.setStudentId(studentId);
        booking.setTutorId(request.getTutorId());
        booking.setMessage(request.getMessage());
        booking.setBusinessId(request.getBusinessId());
        booking.setPrice(new BigDecimal(String.valueOf(request.getPrice())));
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Log status change
        logStatusChange(savedBooking.getId(), null, BookingStatus.PENDING, studentId, "Booking created");

        // TODO: Send notification to tutor
        // notificationService.notifyTutorOfNewBooking(savedBooking);

        return savedBooking;
    }

    /**
     * Check if student has booked a specific tutor
     */
    @Transactional(readOnly = true)
    public Optional<Booking> checkBookingStatus(Long studentId, Long tutorId) {
        return bookingRepository.findActiveBooking(studentId, tutorId);
    }

    /**
     * Get all bookings for a student with tutor info
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getStudentBookings(Long studentId) {
        List<Booking> bookings = bookingRepository.findAllByStudentIdWithTutorInfo(studentId);
        return bookings.stream()
                .map(BookingResponse::fromBooking)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a tutor with student info
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getTutorBookings(Long tutorId) {
        List<Booking> bookings = bookingRepository.findAllByTutorIdWithStudentInfo(tutorId);
        return bookings.stream()
                .map(BookingResponse::fromBooking)
                .collect(Collectors.toList());
    }

    /**
     * Get pending bookings for a tutor
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getTutorPendingBookings(Long tutorId) {
        List<Booking> bookings = bookingRepository.findAllByTutorIdAndStatusOrderByCreatedAtDesc(
                tutorId, BookingStatus.PENDING
        );
        return bookings.stream()
                .map(BookingResponse::fromBooking)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a booking (student action)
     */
    @Transactional
    public Booking cancelBooking(Long bookingId, Long studentId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify the booking belongs to this student
        if (!booking.getStudentId().equals(studentId)) {
            throw new IllegalStateException("You can only cancel your own bookings");
        }

        // Can only cancel pending bookings
        if (!booking.getStatus().canBeCancelled()) {
            throw new IllegalStateException("Cannot cancel booking with status: " + booking.getStatus().getLabel());
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);

        Booking savedBooking = bookingRepository.save(booking);

        // Log status change
        logStatusChange(bookingId, oldStatus, BookingStatus.CANCELLED, studentId, "Cancelled by student");

        // TODO: Notify tutor
        // notificationService.notifyTutorOfCancellation(savedBooking);

        return savedBooking;
    }

    /**
     * Tutor responds to a booking (accept/reject)
     */
    @Transactional
    public Booking respondToBooking(Long bookingId, Long tutorId, TutorResponseRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify the booking is for this tutor
        if (!booking.getTutorId().equals(tutorId)) {
            throw new IllegalStateException("This booking is not for you");
        }

        // Can only respond to pending bookings
        if (!booking.getStatus().canBeRespondedTo()) {
            throw new IllegalStateException("Cannot respond to booking with status: " + booking.getStatus().getLabel());
        }

        BookingStatus oldStatus = booking.getStatus();
        BookingStatus newStatus;
        String actionDescription;

        if (request.isAccept()) {
            newStatus = BookingStatus.ACCEPTED;
            actionDescription = "Accepted by tutor";
        } else if (request.isReject()) {
            newStatus = BookingStatus.REJECTED;
            actionDescription = "Rejected by tutor";
        } else {
            throw new IllegalArgumentException("Invalid status. Use 1 for accept or 2 for reject");
        }

        booking.setStatus(newStatus);
        Booking savedBooking = bookingRepository.save(booking);

        // Log status change
        logStatusChange(bookingId, oldStatus, newStatus, tutorId,
                actionDescription + (request.getReason() != null ? ". Reason: " + request.getReason() : ""));

        // TODO: Notify student
        // notificationService.notifyStudentOfResponse(savedBooking);

        return savedBooking;
    }

    /**
     * Get a single booking by ID
     */
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    /**
     * Get booking status history
     */
    @Transactional(readOnly = true)
    public List<BookingStatusHistory> getBookingHistory(Long bookingId) {
        return historyRepository.findAllByBookingIdOrderByChangedAtDesc(bookingId);
    }

    /**
     * Count pending bookings for a tutor
     */
    @Transactional(readOnly = true)
    public long countPendingBookings(Long tutorId) {
        return bookingRepository.countByTutorIdAndStatus(tutorId, BookingStatus.PENDING);
    }

    /**
     * Helper method to log status changes
     */
    private void logStatusChange(Long bookingId, BookingStatus oldStatus,
                                 BookingStatus newStatus, Long changedBy, String reason) {
        BookingStatusHistory history = new BookingStatusHistory();
        history.setBookingId(bookingId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setReason(reason);

        historyRepository.save(history);
    }
}