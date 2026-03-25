package com.coursecompass.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TutorDashboardStats {
    private Integer totalStudents;
    private Integer studentsThisMonth;
    private BigDecimal monthlyEarnings;
    private BigDecimal totalEarnings;
    private BigDecimal lastMonthEarnings;
    private Double averageRating;   // ← new
    private Long totalReviews;      // ← new
    private List<MonthlyEarningsDTO> earningsChart;
}