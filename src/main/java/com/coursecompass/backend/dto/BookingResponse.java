package com.coursecompass.backend.dto;

import com.coursecompass.backend.domain.Booking;
import com.coursecompass.backend.domain.BookingStatus;

import java.time.LocalDateTime;

/**
 * DTO for booking response
 */
public class BookingResponse {

    private Long id;
    private Long studentId;
    private Long tutorId;
    private Integer status;
    private String statusLabel;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tutor info (for student view)
    private String tutorName;
    private String tutorImage;
    private String tutorSubject;

    // Student info (for tutor view)
    private String studentName;

    // Constructors
    public BookingResponse() {
    }

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.studentId = booking.getStudentId();
        this.tutorId = booking.getTutorId();
        this.status = booking.getStatus().getCode();
        this.statusLabel = booking.getStatus().getLabel();
        this.message = booking.getMessage();
        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
        this.tutorName = booking.getTutorName();
        this.tutorImage = booking.getTutorImage();
        this.tutorSubject = booking.getTutorSubject();
        this.studentName = booking.getStudentName();
    }

    // Static factory method
    public static BookingResponse fromBooking(Booking booking) {
        return new BookingResponse(booking);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getTutorImage() {
        return tutorImage;
    }

    public void setTutorImage(String tutorImage) {
        this.tutorImage = tutorImage;
    }

    public String getTutorSubject() {
        return tutorSubject;
    }

    public void setTutorSubject(String tutorSubject) {
        this.tutorSubject = tutorSubject;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}