package com.coursecompass.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for tutor responding to a booking (accept/reject)
 */
public class TutorResponseRequest {

    @NotNull(message = "Status is required")
    private Integer status; // 1 = ACCEPTED, 2 = REJECTED

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    // Constructors
    public TutorResponseRequest() {
    }

    public TutorResponseRequest(Integer status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    // Getters and Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Check if this is an accept response
     */
    public boolean isAccept() {
        return status != null && status == 1;
    }

    /**
     * Check if this is a reject response
     */
    public boolean isReject() {
        return status != null && status == 2;
    }
}