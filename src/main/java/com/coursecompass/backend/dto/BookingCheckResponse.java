package com.coursecompass.backend.dto;

import com.coursecompass.backend.domain.Booking;

/**
 * DTO for checking if a student has booked a specific tutor
 */
public class BookingCheckResponse {

    private boolean hasBooking;
    private BookingResponse booking;

    // Constructors
    public BookingCheckResponse() {
        this.hasBooking = false;
    }

    public BookingCheckResponse(boolean hasBooking, Booking booking) {
        this.hasBooking = hasBooking;
        if (booking != null) {
            this.booking = new BookingResponse(booking);
        }
    }

    public static BookingCheckResponse noBooking() {
        return new BookingCheckResponse(false, null);
    }

    public static BookingCheckResponse withBooking(Booking booking) {
        return new BookingCheckResponse(true, booking);
    }

    // Getters and Setters
    public boolean isHasBooking() {
        return hasBooking;
    }

    public void setHasBooking(boolean hasBooking) {
        this.hasBooking = hasBooking;
    }

    public BookingResponse getBooking() {
        return booking;
    }

    public void setBooking(BookingResponse booking) {
        this.booking = booking;
    }
}