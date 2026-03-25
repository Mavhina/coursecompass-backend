package com.coursecompass.backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UniversityGuideDTO {

    private Long universityId;
    private String logoUrl;
    private String universityName;

    private ApplicationsDTO applications;
    private List<String> requiredDocuments;
    private FeesDTO fees;
    private AccommodationDTO accommodation;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApplicationsDTO {
        private LocalDate openDate;
        private LocalDate closeDate;
        private boolean lateAvailable;
        private String method;
        private String notes;
        private String viewProspectus;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FeesDTO {
        private Integer applicationFee;
        private Integer registrationFee;
        private Integer tuitionFeeMin;
        private Integer tuitionFeeMax;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AccommodationDTO {
        private boolean onCampusAvailable;
        private Integer onCampusFeeMin;
        private Integer onCampusFeeMax;
        private Integer privateFeeMin;
        private Integer privateFeeMax;
        private Double distanceToCampusKm;
        private String transportNotes;
    }
}
