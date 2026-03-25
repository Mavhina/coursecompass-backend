package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.UniversityGuide;
import com.coursecompass.backend.dto.UniversityGuideDTO;
import com.coursecompass.backend.repository.UniversityGuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UniversityGuideService {

    private final UniversityGuideRepository guideRepo;

    public UniversityGuideService(UniversityGuideRepository guideRepo) {
        this.guideRepo = guideRepo;
    }

    public UniversityGuideDTO getGuide(Long universityId) {
        UniversityGuide guide = guideRepo.findByUniversity_Id(universityId)
                .orElseThrow(() -> new RuntimeException("No guide found for university: " + universityId));

        return UniversityGuideDTO.builder()
                .universityId(guide.getUniversity().getId())
                .universityName(guide.getUniversity().getName())
                .logoUrl(guide.getLogoUrl())
                .applications(UniversityGuideDTO.ApplicationsDTO.builder()
                        .openDate(guide.getOpenDate())
                        .closeDate(guide.getCloseDate())
                        .lateAvailable(Boolean.TRUE.equals(guide.getLateAvailable()))
                        .method(guide.getMethod())
                        .notes(guide.getNotes())
                        .viewProspectus(guide.getViewProspectus())
                        .build())
                .requiredDocuments(
                        guide.getRequiredDocuments().stream()
                                .map(d -> d.getDocumentName())
                                .collect(Collectors.toList())
                )
                .fees(guide.getFees() == null ? null : UniversityGuideDTO.FeesDTO.builder()
                        .applicationFee(guide.getFees().getApplicationFee())
                        .registrationFee(guide.getFees().getRegistrationFee())
                        .tuitionFeeMin(guide.getFees().getTuitionFeeMin())
                        .tuitionFeeMax(guide.getFees().getTuitionFeeMax())
                        .build())
                .accommodation(guide.getAccommodation() == null ? null : UniversityGuideDTO.AccommodationDTO.builder()
                        .onCampusAvailable(Boolean.TRUE.equals(guide.getAccommodation().getOnCampusAvailable()))
                        .onCampusFeeMin(guide.getAccommodation().getOnCampusFeeMin())
                        .onCampusFeeMax(guide.getAccommodation().getOnCampusFeeMax())
                        .privateFeeMin(guide.getAccommodation().getPrivateFeeMin())
                        .privateFeeMax(guide.getAccommodation().getPrivateFeeMax())
                        .distanceToCampusKm(guide.getAccommodation().getDistanceToCampusKm())
                        .transportNotes(guide.getAccommodation().getTransportNotes())
                        .build())
                .build();
    }

    public List<UniversityGuideDTO> getAllGuides() {
        return guideRepo.findAll().stream()
                .map(g -> getGuide(g.getUniversity().getId()))
                .collect(Collectors.toList());
    }
}
