package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.TutorStudentDTO;
import com.coursecompass.backend.repository.BookingRepository;
import com.coursecompass.backend.repository.BookingStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorStudentsService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingStatusHistoryRepository historyRepository;

    // ── All accepted students (no month filter) ──
    public List<TutorStudentDTO> getAllStudents(Long tutorId) {
        List<Object[]> rows = bookingRepository.findAcceptedByTutorIdWithStudentInfo(tutorId);
        return rows.stream().map(this::rowToDTO).collect(Collectors.toList());
    }

    public List<TutorStudentDTO> getStudentsByMonth(Long tutorId, int year, int month) {
        List<Object[]> rows = bookingRepository.findAcceptedByTutorIdAndMonth(tutorId, year, month);
        return rows.stream().map(this::rowToDTO).collect(Collectors.toList());
    }

    private TutorStudentDTO rowToDTO(Object[] row) {
        TutorStudentDTO dto = new TutorStudentDTO();
        dto.setBookingId(((Number) row[0]).longValue());                              // b.id
        dto.setStudentId(row[1] != null ? ((Number) row[1]).longValue() : null);      // b.student_id (null = external)
        dto.setExternalStudentId(row[10] != null ? ((Number) row[10]).longValue() : null); // b.external_student_id
        dto.setPrice(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO);
        dto.setPaymentStatus(row[5] != null
                ? PaymentStatus.values()[((Number) row[5]).intValue()]
                : PaymentStatus.PENDING);
        dto.setPaymentDueDate(row[6] != null
                ? LocalDate.parse(row[6].toString())
                : null);
        dto.setBookedAt(row[7] != null
                ? LocalDateTime.parse(row[7].toString().replace(" ", "T").substring(0, 19))
                : null);
        dto.setStudentName(row[8] != null ? row[8].toString() : "Unknown");
        dto.setStudentEmail(row[9] != null ? row[9].toString() : "");
        return dto;
    }


    // ── Tutor updates payment status → log to history ──
    @Transactional
    public void updatePaymentStatus(Long bookingId, PaymentStatus newStatus, Long tutorId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        PaymentStatus oldStatus = booking.getPaymentStatus();
        booking.setPaymentStatus(newStatus);
        bookingRepository.save(booking);

        BookingStatusHistory log = new BookingStatusHistory();
        log.setBookingId(bookingId);
        log.setOldPaymentStatus(oldStatus);
        log.setNewPaymentStatus(newStatus);
        log.setNewStatus(booking.getStatus());  // ← required, not null
        log.setChangedBy(tutorId);
        log.setReason("Payment status updated by tutor");
        historyRepository.save(log);
    }

    // ── Tutor sets payment due date → log to history ──
    @Transactional
    public void updatePaymentDueDate(Long bookingId, LocalDate dueDate, Long tutorId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        booking.setPaymentDueDate(dueDate);
        bookingRepository.save(booking);

        BookingStatusHistory log = new BookingStatusHistory();
        log.setBookingId(bookingId);
        log.setNewStatus(booking.getStatus());  // ← required, not null
        log.setChangedBy(tutorId);
        log.setReason("Payment due date updated to: " + dueDate);
        historyRepository.save(log);
    }

    private TutorStudentDTO toDTO(Booking b) {
        TutorStudentDTO dto = new TutorStudentDTO();
        dto.setBookingId(b.getId());
        dto.setStudentId(b.getStudentId());
        dto.setStudentName(b.getStudentName());    // from @Transient — set by native query
        dto.setStudentEmail(b.getStudentEmail());  // from @Transient — set by native query
        dto.setPrice(b.getPrice());
        dto.setPaymentStatus(b.getPaymentStatus());
        dto.setPaymentDueDate(b.getPaymentDueDate());
        dto.setBookedAt(b.getCreatedAt());
        return dto;
    }
}