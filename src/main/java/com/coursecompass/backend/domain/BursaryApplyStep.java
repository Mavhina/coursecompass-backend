package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bursary_apply_steps")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BursaryApplyStep {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "bursary_id", nullable = false)
    private Bursary bursary;

    @Column(nullable = false)
    private Integer stepOrder;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String stepText;
}
