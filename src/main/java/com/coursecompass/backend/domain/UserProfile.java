package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String citizenship = "ZA";
    private Long householdIncome;
    private Integer aps;

    @Enumerated(EnumType.STRING)
    private StudyLevel studyLevel;

    @Enumerated(EnumType.STRING)
    private InstitutionType institutionType;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserProfileField> fieldsInterested = new ArrayList<>();
}
