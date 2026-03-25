package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bursary_institutions")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class BursaryInstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bursary_id", nullable = false)
    private Long bursaryId;

    @Column(nullable = false)
    private String institution;
}
