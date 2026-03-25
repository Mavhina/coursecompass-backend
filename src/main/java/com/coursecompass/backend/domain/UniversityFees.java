package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "university_fees")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UniversityFees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "guide_id", nullable = false, unique = true)
    private UniversityGuide guide;

    private Integer applicationFee;
    private Integer registrationFee;
    private Integer tuitionFeeMin;
    private Integer tuitionFeeMax;
}
