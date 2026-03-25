package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "booking_status_history",
        indexes = {
                @Index(name = "idx_history_booking", columnList = "booking_id"),
                @Index(name = "idx_history_changed_at", columnList = "changed_at")
        }
)
public class BookingStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "old_status")
    private BookingStatus oldStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "new_status", nullable = false)
    private BookingStatus newStatus;

    // ── NEW: track payment status changes ──
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "old_payment_status")
    private PaymentStatus oldPaymentStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "new_payment_status")
    private PaymentStatus newPaymentStatus;

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    public BookingStatusHistory() {
    }

    // Constructor for booking status change (existing)
    public BookingStatusHistory(Long bookingId, BookingStatus oldStatus,
                                BookingStatus newStatus, Long changedBy, String reason) {
        this.bookingId = bookingId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
    }

    // ── NEW: Constructor for payment status change ──
    public BookingStatusHistory(Long bookingId, PaymentStatus oldPaymentStatus,
                                PaymentStatus newPaymentStatus, Long changedBy, String reason) {
        this.bookingId = bookingId;
        this.oldPaymentStatus = oldPaymentStatus;
        this.newPaymentStatus = newPaymentStatus;
        this.changedBy = changedBy;
        this.reason = reason;
    }

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}