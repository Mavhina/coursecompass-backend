package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutor/sessions")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SessionController {

    @Autowired private SessionService sessionService;

    private Long getTutorId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }

    @GetMapping
    public ApiResponse<List<SessionDTO>> getSessions(
            Authentication auth,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ApiResponse.success(sessionService.getSessions(getTutorId(auth), status, year, month));
    }

    @PostMapping
    public ApiResponse<List<SessionDTO>> createSessions(
            Authentication auth,
            @Valid @RequestBody CreateSessionRequest request) {
        return ApiResponse.success(sessionService.createSessions(getTutorId(auth), request));
    }

    @PatchMapping("/{sessionId}/status")
    public ApiResponse<SessionDTO> updateStatus(
            Authentication auth,
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body) {
        return ApiResponse.success(sessionService.updateStatus(getTutorId(auth), sessionId, body.get("status")));
    }
}