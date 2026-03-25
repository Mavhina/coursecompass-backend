package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aps_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApsRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    private int minSubjects; // e.g., Best 6
    private boolean includeLifeOrientation; // true if LO is counted
    private String calculationMethod; // e.g., BEST_N, ALL
}
