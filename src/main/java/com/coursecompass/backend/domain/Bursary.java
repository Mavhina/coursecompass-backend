package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bursaries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Bursary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BursaryType type;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BursaryStatus status;

    private LocalDate openDate;
    private LocalDate closingDate;

    @Enumerated(EnumType.STRING)
    private FundingType fundingType;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String applyUrl;

    // quick filtering fields
    private Integer minAps;
    private Long incomeMax;
    private Boolean bonded;

    private LocalDateTime lastUpdated;

    // child collections
    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryStudyLevel> studyLevels = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryInstitutionType> institutionTypes = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryField> fields = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryCover> covers = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryRequiredDocument> requiredDocuments = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryRequirementBullet> requirementBullets = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BursaryFaq> faqs = new ArrayList<>();

    @OneToMany(mappedBy = "bursary", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<BursaryApplyStep> applySteps = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (lastUpdated == null) lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
