package com.coursecompass.backend.dto;

import com.coursecompass.backend.domain.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TutorStudentDTO {
    private Long bookingId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private BigDecimal price;
    private PaymentStatus paymentStatus;  // 0=PENDING, 1=PAID, 2=OVERDUE
    private LocalDate paymentDueDate;
    private LocalDateTime bookedAt;
    private Long externalStudentId;

}