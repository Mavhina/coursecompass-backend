package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.service.FeeFundApplicationQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fee-fund/applications")
public class FeeFundApplicationQueryController {

    private final FeeFundApplicationQueryService service;

    public FeeFundApplicationQueryController(FeeFundApplicationQueryService service) {
        this.service = service;
    }

    // =============================================
    // GET /api/fee-fund/applications/me
    // =============================================
    @GetMapping("/me")
    public FeeFundApplicationListResponseDTO myApplications(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = (User) auth.getPrincipal();
        return service.listMine(user, page, size);
    }

    // =============================================
    // GET /api/fee-fund/applications/{id}
    // =============================================
    @GetMapping("/{id}")
    public FeeFundApplicationDetailsDTO getOne(
            Authentication auth,
            @PathVariable Long id
    ) {
        User user = (User) auth.getPrincipal();
        return service.getDetails(user, id);
    }
}
