package com.coursecompass.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_university_requirement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUniversityRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Course relation
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // University relation
    @ManyToOne
    @JoinColumn(name = "university_id")
    @JsonIgnore
    private University university;

    // APS requirement
    @Column(name = "minimum_aps", nullable = false)
    private Integer minimumAps;  // <-- Make sure this matches the getter you call
}
