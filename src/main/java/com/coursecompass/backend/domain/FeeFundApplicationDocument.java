package com.coursecompass.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fee_fund_application_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeFundApplicationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private FeeFundApplication application;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_provider", nullable = false, length = 20)
    private String storageProvider; // LOCAL

    @Column(name = "storage_key", nullable = false)
    private String storageKey; // file path or key

    @Column(name = "public_url")
    private String publicUrl; // optional if you expose downloads

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
