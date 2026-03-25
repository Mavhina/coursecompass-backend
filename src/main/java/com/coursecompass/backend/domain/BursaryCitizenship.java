package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bursary_citizenships")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class BursaryCitizenship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bursary_id", nullable = false)
    private Long bursaryId;

    @Column(nullable = false)
    private String citizenship;
}
