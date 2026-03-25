package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile_fields")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileField {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldCategory field;
}
