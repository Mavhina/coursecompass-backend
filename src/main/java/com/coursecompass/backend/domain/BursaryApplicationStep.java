package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bursary_application_steps")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class BursaryApplicationStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bursary_id", nullable = false)
    private Long bursaryId;

    @Column(nullable = false)
    private int stepOrder;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String stepText;
}
