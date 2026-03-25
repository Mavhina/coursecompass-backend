package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutor_business_success_stories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorBusinessSuccessStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private TutorBusiness business;

    private String student;
    private String subject;
    private String improvement;

    @Column(columnDefinition = "TEXT")
    private String story;
}
