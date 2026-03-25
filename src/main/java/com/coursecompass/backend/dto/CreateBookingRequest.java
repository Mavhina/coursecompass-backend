package com.coursecompass.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating a new booking request
 */
@Setter
@Getter
public class CreateBookingRequest {

    // Getters and Setters
    @NotNull(message = "Tutor ID is required")
    private Long tutorId;

    @NotNull(message = "Business ID is required")
    private Long businessId;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

    public CreateBookingRequest() {}
}