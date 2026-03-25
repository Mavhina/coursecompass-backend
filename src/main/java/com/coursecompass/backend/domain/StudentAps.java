package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_aps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Column(name = "aps_value", nullable = false)
    private int apsValue;

    @Column(name = "calculation_date")
    private LocalDateTime calculationDate = LocalDateTime.now();
}
