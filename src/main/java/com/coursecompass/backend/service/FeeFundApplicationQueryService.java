package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.FeeFundApplication;
import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.repository.FeeFundApplicationRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class FeeFundApplicationQueryService {

    private final FeeFundApplicationRepository appRepo;

    public FeeFundApplicationQueryService(FeeFundApplicationRepository appRepo) {
        this.appRepo = appRepo;
    }

    // =============================================
    // GET /api/fee-fund/applications/me
    // =============================================
    public FeeFundApplicationListResponseDTO listMine(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FeeFundApplication> paged = appRepo.findByUserId(user.getId(), pageable);

        var items = paged.getContent().stream().map(app ->
                FeeFundApplicationListResponseDTO.ItemDTO.builder()
                        .id(app.getId())
                        .university(FeeFundApplicationListResponseDTO.UniversityDTO.builder()
                                .id(app.getUniversity().getId())
                                .name(app.getUniversity().getName())
                                .build())
                        .programme(app.getProgramme())
                        .studentNumber(app.getStudentNumber())
                        .amountRequested(app.getAmountRequested())
                        .deadlineDate(app.getDeadlineDate().toString())
                        .status(app.getStatus())
                        .createdAt(app.getCreatedAt().toString())
                        .updatedAt(app.getUpdatedAt().toString())
                        .documentsCount(app.getDocuments() == null ? 0 : app.getDocuments().size())
                        .build()
        ).toList();

        return FeeFundApplicationListResponseDTO.builder()
                .items(items)
                .page(FeeFundApplicationListResponseDTO.PageDTO.builder()
                        .number(paged.getNumber())
                        .size(paged.getSize())
                        .totalElements(paged.getTotalElements())
                        .totalPages(paged.getTotalPages())
                        .build())
                .build();
    }

    // =============================================
    // GET /api/fee-fund/applications/{id}
    // =============================================
    public FeeFundApplicationDetailsDTO getDetails(User viewer, Long id) {

        FeeFundApplication app = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        boolean isOwner = app.getUser().getId().equals(viewer.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(viewer.getRole());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Forbidden");
        }

        return FeeFundApplicationDetailsDTO.builder()
                .id(app.getId())
                .university(FeeFundApplicationDetailsDTO.UniversityDTO.builder()
                        .id(app.getUniversity().getId())
                        .name(app.getUniversity().getName())
                        .location(app.getUniversity().getLocation())
                        .build())
                .programme(app.getProgramme())
                .studentNumber(app.getStudentNumber())
                .amountRequested(app.getAmountRequested())
                .deadlineDate(app.getDeadlineDate().toString())
                .motivation(app.getMotivation())
                .status(app.getStatus())
                .adminNotes(app.getAdminNotes())
                .requestedMoreInfo(app.getRequestedMoreInfo())
                .createdAt(app.getCreatedAt().toString())
                .updatedAt(app.getUpdatedAt().toString())
                .documents(app.getDocuments().stream().map(d ->
                        FeeFundApplicationDetailsDTO.DocumentDTO.builder()
                                .id(d.getId())
                                .originalFileName(d.getOriginalFileName())
                                .contentType(d.getContentType())
                                .fileSize(d.getFileSize())
                                .publicUrl(d.getPublicUrl())
                                .build()
                ).toList())
                .build();
    }
}
