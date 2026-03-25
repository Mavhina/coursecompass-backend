package com.coursecompass.backend.dto;

import com.coursecompass.backend.domain.SessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class CreateSessionRequest {

    @NotNull(message = "Session type is required")
    private SessionType type;

    private Long studentId;
    private Long externalStudentId;
    private Long groupId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "Duration is required")
    private BigDecimal duration;

    @NotBlank(message = "Mode is required")
    private String mode;

    private String notes;
    private boolean recurring;
    private String recurFrequency; // Daily | Weekly | Biweekly | Monthly
    private LocalDate recurEndDate;
}