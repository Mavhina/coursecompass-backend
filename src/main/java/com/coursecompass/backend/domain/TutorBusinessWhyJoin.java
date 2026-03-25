package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutor_business_why_join")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorBusinessWhyJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private TutorBusiness business;

    @Column(nullable = false)
    private String item;
}
