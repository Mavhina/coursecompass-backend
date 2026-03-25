package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.ApiResponse;
import com.coursecompass.backend.dto.TutorBusinessProfileDTO;
import com.coursecompass.backend.service.TutorBusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutor/business")
@RequiredArgsConstructor
public class TutorBusinessController {

    private final TutorBusinessProfileService service;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User u) return u;
        throw new RuntimeException("Not authenticated");
    }

    /** Get the tutor's own business profile (or empty shell if none exists yet) */
    @GetMapping
    public ApiResponse<TutorBusinessProfileDTO> getMyBusiness() {
        User tutor = getCurrentUser();
        return ApiResponse.success(service.getOrEmpty(tutor.getId()));
    }

    /** Create or update the full business profile in one call */
    @PutMapping
    public ApiResponse<TutorBusinessProfileDTO> saveMyBusiness(
            @RequestBody TutorBusinessProfileDTO dto) {
        User tutor = getCurrentUser();
        return ApiResponse.success(service.save(tutor.getId(), dto));
    }
}