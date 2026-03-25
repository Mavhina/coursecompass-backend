package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fee_fund_applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeFundApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who submitted (logged-in user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // which university
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Column(name = "student_number", nullable = false, length = 50)
    private String studentNumber;

    @Column(nullable = false, length = 180)
    private String programme;

    @Column(name = "amount_requested", nullable = false)
    private Integer amountRequested;

    @Column(name = "deadline_date", nullable = false)
    private LocalDate deadlineDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivation;

    @Column(nullable = false, length = 30)
    private String status = "SUBMITTED";

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "requested_more_info")
    private Boolean requestedMoreInfo = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeeFundApplicationDocument> documents = new ArrayList<>();

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
