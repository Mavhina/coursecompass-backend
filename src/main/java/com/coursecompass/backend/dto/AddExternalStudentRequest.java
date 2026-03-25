package com.coursecompass.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class AddExternalStudentRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String email;
    private String phone;
    @NotNull(message = "Business ID is required")
    private Long businessId;
    private BigDecimal price;

}