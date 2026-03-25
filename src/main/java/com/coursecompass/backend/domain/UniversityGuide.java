package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "university_guides")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UniversityGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "university_id", nullable = false, unique = true)
    private University university;

    private String logoUrl;

    // Applications
    private LocalDate openDate;
    private LocalDate closeDate;
    private Boolean lateAvailable;

    private String method; // ONLINE / MANUAL / BOTH

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "view_prospectus", length = 500)
    private String viewProspectus;

    // Required docs
    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UniversityRequiredDocument> requiredDocuments = new ArrayList<>();

    // Fees (1:1)
    @OneToOne(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private UniversityFees fees;

    // Accommodation (1:1)
    @OneToOne(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private UniversityAccommodation accommodation;
}
