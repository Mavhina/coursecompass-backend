package com.coursecompass.backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BursaryDetailsDTO {
    private Long id;
    private String slug;
    private String name;
    private String provider;
    private String type;
    private String logoUrl;
    private String status;

    private LocalDate openDate;
    private LocalDate closingDate;

    private String fundingType;

    private List<String> studyLevels;
    private List<String> institutionTypes;
    private List<String> institutions; // simple string list for now
    private List<String> fields;

    private WhatItCoversDTO whatItCovers;
    private RequirementsDTO requirements;

    private List<String> requiredDocuments;

    private HowToApplyDTO howToApply;
    private List<FaqDTO> faqs;

    private OffsetDateTime lastUpdated;
}
