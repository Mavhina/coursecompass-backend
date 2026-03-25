package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.UniversityGuideDTO;
import com.coursecompass.backend.service.UniversityGuideService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class    UniversityGuideController {

    private final UniversityGuideService guideService;

    public UniversityGuideController(UniversityGuideService guideService) {
        this.guideService = guideService;
    }

    @GetMapping("/university-guide/{universityId}")
    public UniversityGuideDTO getUniversityGuide(@PathVariable Long universityId) {
        return guideService.getGuide(universityId);
    }

    // ✅ NEW: list all guides
    @GetMapping("/university-guides")
    public List<UniversityGuideDTO> getAllUniversityGuides() {
        return guideService.getAllGuides();
    }
}
