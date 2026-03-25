package com.coursecompass.backend.domain;

/**
 * Enum representing the different statuses a booking can have
 */
public enum BookingStatus {
    PENDING(0, "Pending", "Waiting for tutor to respond"),
    ACCEPTED(1, "Accepted", "Tutor has accepted the booking"),
    REJECTED(2, "Rejected", "Tutor has rejected the booking"),
    CANCELLED(3, "Cancelled", "Student has cancelled the booking");

    private final int code;
    private final String label;
    private final String description;

    BookingStatus(int code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get BookingStatus from integer code
     */
    public static BookingStatus fromCode(int code) {
        for (BookingStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid booking status code: " + code);
    }

    /**
     * Check if a booking can be cancelled (only PENDING bookings can be cancelled)
     */
    public boolean canBeCancelled() {
        return this == PENDING;
    }

    /**
     * Check if a booking can be responded to (only PENDING bookings can be responded to)
     */
    public boolean canBeRespondedTo() {
        return this == PENDING;
    }
}