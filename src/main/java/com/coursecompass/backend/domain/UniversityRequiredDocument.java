package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "university_required_documents")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UniversityRequiredDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    private UniversityGuide guide;

    @Column(nullable = false)
    private String documentName;
}
