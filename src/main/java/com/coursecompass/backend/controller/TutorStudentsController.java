package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.AddExternalStudentRequest;
import com.coursecompass.backend.dto.ApiResponse;
import com.coursecompass.backend.dto.TutorBusinessSummaryDTO;
import com.coursecompass.backend.dto.TutorStudentDTO;
import com.coursecompass.backend.repository.BookingRepository;
import com.coursecompass.backend.repository.ExternalStudentRepository;
import com.coursecompass.backend.repository.TutorBusinessRepository;
import com.coursecompass.backend.repository.UserRepository;
import com.coursecompass.backend.service.ResendEmailService;
import com.coursecompass.backend.service.TutorStudentsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutor/students")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TutorStudentsController {

    @Autowired private TutorStudentsService studentsService;
    @Autowired private UserRepository userRepository;
    @Autowired private ExternalStudentRepository externalStudentRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ResendEmailService emailService;
    @Autowired private TutorBusinessRepository tutorBusinessRepository;

    // GET /api/tutor/students/businesses
    @GetMapping("/businesses")
    public ApiResponse<List<TutorBusinessSummaryDTO>> getTutorBusinesses() {
        Long tutorId = getTutorId();
        List<TutorBusiness> businesses = tutorBusinessRepository.findAllByOwner_Id(tutorId);
        List<TutorBusinessSummaryDTO> result = businesses.stream()
                .map(b -> new TutorBusinessSummaryDTO(b.getId(), b.getName()))
                .toList();
        return ApiResponse.success(result);
    }

    // GET /api/tutor/students/search?q=email_or_name
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchStudent(@RequestParam String q) {
        return userRepository.findByEmailIgnoreCase(q)
                .or(() -> userRepository.findFirstByFullNameContainingIgnoreCase(q))
                .map(user -> {
                    Map<String, Object> data = Map.of(
                            "id", user.getId(),
                            "fullName", user.getFullName(),
                            "email", user.getEmail()
                    );
                    return ApiResponse.<Map<String, Object>>success(data);
                })
                .orElse(ApiResponse.success(null));
    }

    // POST /api/tutor/students/add-platform
    @PostMapping("/add-platform")
    public ApiResponse<String> addPlatformStudent(@RequestBody Map<String, Long> body) {
        Long tutorId = getTutorId();
        Long studentId = body.get("userId");

        Booking booking = new Booking();
        booking.setTutorId(tutorId);
        booking.setStudentId(studentId);
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        bookingRepository.save(booking);

        return ApiResponse.success("Student added successfully");
    }

    // POST /api/tutor/students/add-external
    @PostMapping("/add-external")
    public ApiResponse<String> addExternalStudent(
            @Valid @RequestBody AddExternalStudentRequest request) {
        Long tutorId = getTutorId();
        User tutor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1. Create external student record
        ExternalStudent external = ExternalStudent.builder()
                .tutorId(tutorId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        ExternalStudent saved = externalStudentRepository.save(external);

        // 2. Create booking linked to external student
        Booking booking = new Booking();
        booking.setTutorId(tutorId);
        booking.setBusinessId(request.getBusinessId());  // ← ADD THIS
        booking.setExternalStudentId(saved.getId());
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        // Set price - use request price or fetch from business
        if (request.getPrice() != null) {
            booking.setPrice(request.getPrice());
        } else {
            // Fetch from business or set default
            booking.setPrice(BigDecimal.ZERO); // or fetch from tutor_businesses
        }

        bookingRepository.save(booking);

        // 3. Send invite email if email provided
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            emailService.sendInviteEmail(request.getEmail(), request.getName(), tutor.getFullName());
        }

        return ApiResponse.success("Student added successfully");
    }

    // GET /api/tutor/students
    @GetMapping
    public ApiResponse<List<TutorStudentDTO>> getStudents(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            Long tutorId = getTutorId();
            System.out.println("🎯 getStudents called for tutorId: " + tutorId);
            List<TutorStudentDTO> students = (year != null && month != null)
                    ? studentsService.getStudentsByMonth(tutorId, year, month)
                    : studentsService.getAllStudents(tutorId);
            System.out.println("🎯 students found: " + students.size());
            return ApiResponse.success(students);
        } catch (Exception e) {
            System.out.println("❌ ERROR in getStudents: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.success(List.of());
        }
    }

    // PATCH /api/tutor/students/{bookingId}/payment-status
    @PatchMapping("/{bookingId}/payment-status")
    public ApiResponse<String> updatePaymentStatus(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> body) {
        Long tutorId = getTutorId();
        PaymentStatus newStatus = PaymentStatus.valueOf(body.get("paymentStatus"));
        studentsService.updatePaymentStatus(bookingId, newStatus, tutorId);
        return ApiResponse.success("Payment status updated");
    }

    // PATCH /api/tutor/students/{bookingId}/due-date
    @PatchMapping("/{bookingId}/due-date")
    public ApiResponse<String> updateDueDate(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> body) {
        Long tutorId = getTutorId();
        LocalDate dueDate = LocalDate.parse(body.get("dueDate"));
        studentsService.updatePaymentDueDate(bookingId, dueDate, tutorId);
        return ApiResponse.success("Due date updated");
    }

    private Long getTutorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof User user) {
            return user.getId();
        }
        return Long.valueOf(auth.getName());
    }
}