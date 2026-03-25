package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.BursaryMatchesDTO;
import com.coursecompass.backend.service.BursaryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserBursaryController {

    private final BursaryService bursaryService;

    public UserBursaryController(BursaryService bursaryService) {
        this.bursaryService = bursaryService;
    }

    // 4) GET /api/users/me/bursary-matches
    @GetMapping("/bursary-matches")
    public BursaryMatchesDTO myMatches(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bursaryService.getMatches(user);
    }
}
