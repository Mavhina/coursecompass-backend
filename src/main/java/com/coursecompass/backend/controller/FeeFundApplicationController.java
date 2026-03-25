package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.FeeFundApplicationResponseDTO;
import com.coursecompass.backend.service.FeeFundApplicationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fee-fund/applications")
public class FeeFundApplicationController {

    private final FeeFundApplicationService service;

    public FeeFundApplicationController(FeeFundApplicationService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FeeFundApplicationResponseDTO create(
            Authentication auth,
            @RequestParam Long universityId,
            @RequestParam String programme,
            @RequestParam String studentNumber,
            @RequestParam Integer amountRequested,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineDate,
            @RequestParam String motivation,
            @RequestPart(required = false) List<MultipartFile> documents
    ) {
        System.out.println("auth.getName() = " + auth.getName());
        System.out.println("principal class = " + auth.getPrincipal().getClass());
        System.out.println("principal = " + auth.getPrincipal());
        User user = (User) auth.getPrincipal();
        String email = user.getEmail(); // ✅ correct // from JWT (username/email)
        return service.create(email, universityId, programme, studentNumber, amountRequested, deadlineDate, motivation, documents);
    }
}
