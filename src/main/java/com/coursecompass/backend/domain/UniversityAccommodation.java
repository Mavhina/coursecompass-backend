package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "university_accommodation")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UniversityAccommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "guide_id", nullable = false, unique = true)
    private UniversityGuide guide;

    private Boolean onCampusAvailable;
    private Integer onCampusFeeMin;
    private Integer onCampusFeeMax;

    private Integer privateFeeMin;
    private Integer privateFeeMax;

    private Double distanceToCampusKm;

    @Column(columnDefinition = "TEXT")
    private String transportNotes;
}
