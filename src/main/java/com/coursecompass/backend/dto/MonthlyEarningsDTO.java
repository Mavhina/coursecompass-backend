package com.coursecompass.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyEarningsDTO {
    private String month;      // e.g. "Jan", "Feb"
    private BigDecimal earnings;
}