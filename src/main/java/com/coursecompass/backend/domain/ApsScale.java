package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aps_scales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApsScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    private int minPercentage;
    private int maxPercentage;
    private int apsPoints;
}
