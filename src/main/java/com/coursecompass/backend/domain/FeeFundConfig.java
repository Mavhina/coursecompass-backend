package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_fund_config")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeFundConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "applications_open", nullable = false)
    private Boolean applicationsOpen;

    @Column(name = "opens_at")
    private LocalDate opensAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
