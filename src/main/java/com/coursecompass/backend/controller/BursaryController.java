package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.service.BursaryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bursaries")
public class BursaryController {

    private final BursaryService bursaryService;

    public BursaryController(BursaryService bursaryService) {
        this.bursaryService = bursaryService;
    }

    // 1) GET /api/bursaries (List + filters + featured NSFAS)
    @GetMapping
    public BursaryListResponseDTO list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String fundingType,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) Integer apsMin,
            @RequestParam(required = false) Long incomeMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return bursaryService.list(q, status, level, fundingType, field, institutionType, apsMin, incomeMax, page, size);
    }

    // 2) GET /api/bursaries/{id} (Full details)
    @GetMapping("/{id}")
    public BursaryDetailsDTO details(@PathVariable Long id) {
        return bursaryService.getDetails(id);
    }

    // 3) GET /api/bursaries/filters
    @GetMapping("/filters")
    public BursaryFiltersDTO filters() {
        return bursaryService.getFilters();
    }
}
