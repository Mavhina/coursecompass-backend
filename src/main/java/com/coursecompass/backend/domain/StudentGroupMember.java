package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_group_members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudentGroup group;

    private Long studentId;         // platform user (nullable)
    private Long externalStudentId; // external student (nullable)
}