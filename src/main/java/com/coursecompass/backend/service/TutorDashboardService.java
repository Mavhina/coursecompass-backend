package com.coursecompass.backend.service;

import com.coursecompass.backend.dto.MonthlyEarningsDTO;
import com.coursecompass.backend.dto.TutorDashboardStats;
import com.coursecompass.backend.repository.BookingRepository;
import com.coursecompass.backend.repository.TutorBusinessReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorDashboardService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TutorBusinessReviewRepository reviewRepository;

    public TutorDashboardStats getStats(Long tutorUserId) {
        LocalDate now = LocalDate.now();
        LocalDate firstOfMonth = now.withDayOfMonth(1);
        LocalDate lastOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        TutorDashboardStats stats = new TutorDashboardStats();

        // Total accepted students (distinct)
        Long totalStudents = bookingRepository.countDistinctStudentsByTutorId(tutorUserId);
        stats.setTotalStudents(totalStudents != null ? totalStudents.intValue() : 0);

        // New accepted students this month (distinct)
        Long studentsThisMonth = bookingRepository.countDistinctStudentsByTutorIdAndDateRange(
                tutorUserId, firstOfMonth, lastOfMonth);
        stats.setStudentsThisMonth(studentsThisMonth != null ? studentsThisMonth.intValue() : 0);

        // Monthly earnings
        BigDecimal monthlyEarnings = bookingRepository.sumMonthlyEarningsByTutorId(
                tutorUserId, now.getYear(), now.getMonthValue());
        stats.setMonthlyEarnings(monthlyEarnings != null ? monthlyEarnings : BigDecimal.ZERO);

        // Total earnings all time
        BigDecimal totalEarnings = bookingRepository.sumEarningsByTutorId(tutorUserId);
        stats.setTotalEarnings(totalEarnings != null ? totalEarnings : BigDecimal.ZERO);

        // Last month's earnings
        LocalDate lastMonth = now.minusMonths(1);
        BigDecimal lastMonthEarnings = bookingRepository.sumMonthlyEarningsByTutorId(
                tutorUserId, lastMonth.getYear(), lastMonth.getMonthValue());
        stats.setLastMonthEarnings(lastMonthEarnings != null ? lastMonthEarnings : BigDecimal.ZERO);

        // Average rating
        Double avgRating = reviewRepository.avgRatingByTutorId(tutorUserId);
        if (avgRating != null) {
            double rounded = BigDecimal.valueOf(avgRating)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            stats.setAverageRating(rounded);
        } else {
            stats.setAverageRating(null);
        }

        // Total reviews
        Long totalReviews = reviewRepository.countReviewsByTutorId(tutorUserId);
        stats.setTotalReviews(totalReviews != null ? totalReviews : 0L);

        // ── Earnings chart: last 6 months ──
        List<Object[]> rawChart = bookingRepository.findMonthlyEarningsLast6Months(tutorUserId);
        List<MonthlyEarningsDTO> earningsChart = rawChart.stream()
                .map(row -> new MonthlyEarningsDTO(
                        (String) row[0],                                      // month label e.g. "Mar"
                        row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());
        stats.setEarningsChart(earningsChart);

        return stats;
    }
}