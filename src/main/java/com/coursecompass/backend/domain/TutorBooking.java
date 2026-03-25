package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tutor_bookings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_user_id", "business_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // student is a USER in your system
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_user_id", nullable = false)
    private User studentUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private TutorBusiness business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutorBookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
