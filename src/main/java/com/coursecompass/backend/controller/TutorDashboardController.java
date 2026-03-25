package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.Booking;
import com.coursecompass.backend.dto.ApiResponse;
import com.coursecompass.backend.dto.TutorDashboardStats;
import com.coursecompass.backend.repository.BookingRepository;
import com.coursecompass.backend.service.TutorDashboardService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tutor/dashboard")
@CrossOrigin(origins = {"http://localhost:5173"})
public class TutorDashboardController {

    @Autowired
    private TutorDashboardService dashboardService;

    @Autowired
    private BookingRepository bookingRepository;

    @PostConstruct
    public void init() {
        System.out.println("✅✅✅ TutorDashboardController loaded! ✅✅✅");
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TutorDashboardStats>> getStats() {
        Long tutorUserId = getCurrentTutorUserId();
        System.out.println("Getting stats for tutorUserId: " + tutorUserId);
        TutorDashboardStats stats = dashboardService.getStats(tutorUserId);
        return ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        Long tutorUserId = getCurrentTutorUserId();

        // Get all bookings for this tutor user ID
        List<Booking> all = bookingRepository.findByTutorId(tutorUserId);
        List<Booking> accepted = bookingRepository.findAcceptedByTutorId(tutorUserId);
        Long distinctStudents = bookingRepository.countDistinctStudentsByTutorId(tutorUserId);

        String details = all.stream()
                .map(b -> String.format("  id=%d, status=%s, student=%d, tutor=%d",
                        b.getId(), b.getStatus(), b.getStudentId(), b.getTutorId()))
                .collect(Collectors.joining("\n"));

        String result = String.format(
                "JWT UserId (used as tutor_id): %d\nAll bookings: %d\nAccepted bookings: %d\nDistinct students: %d\n\nAll bookings details:\n%s",
                tutorUserId, all.size(), accepted.size(), distinctStudents,
                details.isEmpty() ? "  (none found)" : details
        );

        System.out.println(result);
        return ResponseEntity.ok(ApiResponse.success("Test", result));
    }

    private Long getCurrentTutorUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If principal is a User object, get the ID from it
        if (auth.getPrincipal() instanceof com.coursecompass.backend.domain.User) {
            com.coursecompass.backend.domain.User user =
                    (com.coursecompass.backend.domain.User) auth.getPrincipal();
            return user.getId();
        }

        // Otherwise try to parse as string
        return Long.valueOf(auth.getName());
    }
}