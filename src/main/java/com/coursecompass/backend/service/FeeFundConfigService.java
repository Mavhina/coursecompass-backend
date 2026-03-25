package com.coursecompass.backend.service;

import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.repository.FeeFundConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class FeeFundConfigService {

    private final FeeFundConfigRepository repo;

    public FeeFundConfigService(FeeFundConfigRepository repo) {
        this.repo = repo;
    }

    public FeeFundConfigResponseDTO getConfig() {

        var config = repo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Fee fund config not initialized"));

        return FeeFundConfigResponseDTO.builder()
                .applicationsOpen(config.getApplicationsOpen())
                .opensAt(config.getOpensAt() != null ? config.getOpensAt().toString() : null)
                .build();
    }
}
