package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutor_businesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Link to a real User (role = TUTOR)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    // Business basic info
    @Column(nullable = false)
    private String name;

    private String headline;
    private String mode;        // Online / In-person / Hybrid
    private String location;
    private String bannerImageUrl;

    // About section
    @Column(columnDefinition = "TEXT")
    private String bio;

    // Pricing
    private Integer pricePerMonth;
    private String currency; // e.g. ZAR

    // -------- Child collections ----------
    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BusinessTutor> tutors = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorBusinessWhyJoin> whyJoinItems = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorBusinessSubjectOffered> subjectsOffered = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorBusinessPricingInclude> pricingIncludes = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorBusinessGalleryImage> galleryImages = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorBusinessSuccessStory> successStories = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorBusinessReview> reviews = new ArrayList<>();
}
