package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.Booking;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for tutor booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")  // Configure properly for production
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Create a new booking (Student books a tutor)
     * POST /api/bookings
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        try {
            Long studentId = getCurrentUserId();
            Booking booking = bookingService.createBooking(studentId, request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Booking request sent successfully",
                            new BookingResponse(booking)));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create booking: " + e.getMessage()));
        }
    }

    /**
     * Check if current student has booked a specific tutor
     * GET /api/bookings/check?tutor_id=123
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<BookingCheckResponse>> checkBookingStatus(
            @RequestParam("tutor_id") Long tutorId) {

        try {
            Long studentId = getCurrentUserId();

            return bookingService.checkBookingStatus(studentId, tutorId)
                    .map(booking -> ResponseEntity.ok(
                            ApiResponse.success(BookingCheckResponse.withBooking(booking))))
                    .orElse(ResponseEntity.ok(
                            ApiResponse.success(BookingCheckResponse.noBooking())));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check booking status: " + e.getMessage()));
        }
    }

    /**
     * Get all bookings for current student (with tutor info)
     * GET /api/bookings/my-bookings
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
        try {
            Long studentId = getCurrentUserId();
            List<BookingResponse> bookings = bookingService.getStudentBookings(studentId);

            return ResponseEntity.ok(ApiResponse.success(bookings));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch bookings: " + e.getMessage()));
        }
    }

    /**
     * Get all bookings for current tutor (with student info)
     * GET /api/bookings/tutor-bookings
     */
    @GetMapping("/tutor-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getTutorBookings() {
        try {
            Long tutorId = getCurrentUserId(); // Assuming tutor is also a user
            List<BookingResponse> bookings = bookingService.getTutorBookings(tutorId);

            return ResponseEntity.ok(ApiResponse.success(bookings));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch bookings: " + e.getMessage()));
        }
    }

    /**
     * Get pending bookings for current tutor
     * GET /api/bookings/tutor/pending
     */
    @GetMapping("/tutor/pending")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getTutorPendingBookings() {
        try {
            Long tutorId = getCurrentUserId();
            List<BookingResponse> bookings = bookingService.getTutorPendingBookings(tutorId);

            return ResponseEntity.ok(ApiResponse.success(bookings));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch pending bookings: " + e.getMessage()));
        }
    }

    /**
     * Cancel a booking (Student action)
     * PUT /api/bookings/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable("id") Long bookingId) {

        try {
            Long studentId = getCurrentUserId();
            Booking booking = bookingService.cancelBooking(bookingId, studentId);

            return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully",
                    new BookingResponse(booking)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cancel booking: " + e.getMessage()));
        }
    }

    /**
     * Tutor responds to a booking (accept/reject)
     * PUT /api/bookings/{id}/respond
     */
    @PutMapping("/{id}/respond")
    public ResponseEntity<ApiResponse<BookingResponse>> respondToBooking(
            @PathVariable("id") Long bookingId,
            @Valid @RequestBody TutorResponseRequest request) {

        try {
            Long tutorId = getCurrentUserId();
            Booking booking = bookingService.respondToBooking(bookingId, tutorId, request);

            String message = request.isAccept() ? "Booking accepted" : "Booking rejected";
            return ResponseEntity.ok(ApiResponse.success(message, new BookingResponse(booking)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to respond to booking: " + e.getMessage()));
        }
    }

    /**
     * Get a single booking by ID
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable("id") Long bookingId) {

        try {
            Booking booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(ApiResponse.success(new BookingResponse(booking)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch booking: " + e.getMessage()));
        }
    }

    /**
     * Get count of pending bookings for tutor
     * GET /api/bookings/tutor/pending-count
     */
    @GetMapping("/tutor/pending-count")
    public ResponseEntity<ApiResponse<Long>> getPendingBookingsCount() {
        try {
            Long tutorId = getCurrentUserId();
            long count = bookingService.countPendingBookings(tutorId);

            return ResponseEntity.ok(ApiResponse.success(count));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch count: " + e.getMessage()));
        }
    }

    /**
     * Helper method to get current authenticated user ID
     * Adjust this based on your authentication implementation
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        // Adjust based on your UserDetails implementation
        // Example: return ((UserPrincipal) authentication.getPrincipal()).getId();
        // For now, returning a placeholder - replace with your actual implementation
        Object principal = authentication.getPrincipal();

        // If principal is your custom User class
        if (principal instanceof com.coursecompass.backend.domain.User) {
            return ((com.coursecompass.backend.domain.User) principal).getId();
        }

        // If principal is a Long/String
        if (principal instanceof Long) {
            return (Long) principal;
        }

        if (principal instanceof String) {
            return Long.parseLong((String) principal);
        }

        // Fallback - you should implement this based on your auth system
        throw new IllegalStateException("Unable to extract user ID from authentication");
    }
}