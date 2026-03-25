package com.coursecompass.backend.domain;

public enum PaymentStatus {
    PENDING,   // 0 - booked but not paid
    PAID,      // 1 - paid
    OVERDUE    // 2 - past due date, not paid
}