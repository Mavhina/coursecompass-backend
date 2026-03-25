package com.coursecompass.backend.domain;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bursary_faqs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BursaryFaq {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "bursary_id", nullable = false)
    private Bursary bursary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;
}
