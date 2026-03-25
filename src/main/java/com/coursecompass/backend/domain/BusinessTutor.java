package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_tutors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // belongs to business
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private TutorBusiness business;

    @Column(nullable = false)
    private String name;

    private String imageUrl;
    private String grades;          // "Grades 10–12"
    private Double rating;          // 4.8
    private Integer reviewsCount;   // 120
    private Integer experienceYears;

    // Store subjects as a CSV string to keep it simple
    // e.g. "Mathematics,Physical Sciences"
    @Column(name = "subjects_csv", columnDefinition = "TEXT")
    private String subjectsCsv;
}
