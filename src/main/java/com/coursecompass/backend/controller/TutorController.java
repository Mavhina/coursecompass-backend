package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.TutorCardDTO;
import com.coursecompass.backend.service.TutorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    // ✅ Student can call this to list tutors
    @GetMapping
    public List<TutorCardDTO> getTutors() {
        return tutorService.getAllTutorCards();
    }
}
