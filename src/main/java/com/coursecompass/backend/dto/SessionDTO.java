package com.coursecompass.backend.dto;

import com.coursecompass.backend.domain.SessionStatus;
import com.coursecompass.backend.domain.SessionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class SessionDTO {
    private Long id;
    private SessionType type;
    private Long studentId;
    private Long externalStudentId;
    private String studentName;
    private Long groupId;
    private String groupName;
    private String subject;
    private String topic;
    private LocalDate date;
    private LocalTime startTime;
    private BigDecimal duration;
    private String mode;
    private SessionStatus status;
    private String computedStatus; // COMPLETED if past, else status
    private String notes;
    private boolean recurring;
    private String recurGroupId;
}