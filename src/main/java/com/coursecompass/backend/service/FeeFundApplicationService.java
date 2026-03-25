package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.FeeFundApplicationResponseDTO;
import com.coursecompass.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeeFundApplicationService {

    private final FeeFundApplicationRepository appRepo;
    private final UniversityRepository universityRepo;
    private final UserRepository userRepo;
    private final FeeFundApplicationDocumentRepository docRepo;
    private final FileStorageService fileStorage;

    public FeeFundApplicationService(
            FeeFundApplicationRepository appRepo,
            UniversityRepository universityRepo,
            UserRepository userRepo,
            FeeFundApplicationDocumentRepository docRepo,
            FileStorageService fileStorage
    ) {
        this.appRepo = appRepo;
        this.universityRepo = universityRepo;
        this.userRepo = userRepo;
        this.docRepo = docRepo;
        this.fileStorage = fileStorage;
    }

    @Transactional
    public FeeFundApplicationResponseDTO create(
            String userEmail,
            Long universityId,
            String programme,
            String studentNumber,
            Integer amountRequested,
            LocalDate deadlineDate,
            String motivation,
            List<MultipartFile> documents
    ) {
        String normalizedEmail = userEmail == null ? null : userEmail.trim();

        User user = userRepo.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + normalizedEmail));

        University university = universityRepo.findById(universityId)
                .orElseThrow(() -> new RuntimeException("University not found"));

        FeeFundApplication app = FeeFundApplication.builder()
                .user(user)
                .university(university)
                .programme(programme)
                .studentNumber(studentNumber)
                .amountRequested(amountRequested)
                .deadlineDate(deadlineDate)
                .motivation(motivation)
                .status("SUBMITTED")
                .requestedMoreInfo(false)
                .build();

        app = appRepo.save(app);

        if (documents != null) {
            for (MultipartFile f : documents) {
                if (f == null || f.isEmpty()) continue;

                var stored = fileStorage.store(f, app.getId());

                FeeFundApplicationDocument doc = FeeFundApplicationDocument.builder()
                        .application(app)
                        .originalFileName(stored.originalFileName())
                        .contentType(f.getContentType())
                        .fileSize(f.getSize())
                        .storageProvider("LOCAL")
                        .storageKey(stored.storageKey())
                        .publicUrl(stored.publicUrl())
                        .build();

                docRepo.save(doc);
                app.getDocuments().add(doc);
            }
        }

        return FeeFundApplicationResponseDTO.builder()
                .id(app.getId())
                .userId(user.getId())
                .universityId(university.getId())
                .studentNumber(app.getStudentNumber())
                .programme(app.getProgramme())
                .amountRequested(app.getAmountRequested())
                .deadlineDate(app.getDeadlineDate())
                .motivation(app.getMotivation())
                .status(app.getStatus())
                .documents(app.getDocuments().stream().map(d ->
                        FeeFundApplicationResponseDTO.DocumentDTO.builder()
                                .id(d.getId())
                                .originalFileName(d.getOriginalFileName())
                                .contentType(d.getContentType())
                                .fileSize(d.getFileSize())
                                .storageProvider(d.getStorageProvider())
                                .storageKey(d.getStorageKey())
                                .publicUrl(d.getPublicUrl())
                                .build()
                ).toList())
                .build();
    }

}
