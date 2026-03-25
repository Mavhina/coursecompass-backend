package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "bookings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "tutor_id"}, name = "uk_student_tutor_active")
        },
        indexes = {
                @Index(name = "idx_booking_student", columnList = "student_id"),
                @Index(name = "idx_booking_tutor", columnList = "tutor_id"),
                @Index(name = "idx_booking_status", columnList = "status"),
                @Index(name = "idx_booking_business", columnList = "business_id"),
                @Index(name = "idx_booking_created", columnList = "created_at")
        }
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "tutor_id")
    private Long tutorId;

    @Column(name = "business_id")
    private Long businessId;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    // ── NEW: Payment status (0=PENDING, 1=PAID, 2=OVERDUE) ──
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // ── NEW: Due date set by tutor ──
    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Transient fields for joined data (not persisted)
    @Transient
    private String tutorName;

    @Transient
    private String tutorImage;

    @Transient
    private String tutorSubject;

    @Transient
    private String studentName;

    @Transient
    private String businessTitle;

    @Transient
    private String studentEmail;  // ← new: from users.email

    public Booking() {
    }

    @Column(name = "external_student_id")
    private Long externalStudentId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", tutorId=" + tutorId +
                ", businessId=" + businessId +
                ", price=" + price +
                ", amountPaid=" + amountPaid +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", paymentDueDate=" + paymentDueDate +
                ", createdAt=" + createdAt +
                '}';
    }
}