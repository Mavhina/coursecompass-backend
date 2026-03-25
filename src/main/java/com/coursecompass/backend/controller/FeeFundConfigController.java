package com.coursecompass.backend.controller;

import com.coursecompass.backend.dto.FeeFundConfigResponseDTO;
import com.coursecompass.backend.service.FeeFundConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fee-fund")
public class FeeFundConfigController {

    private final FeeFundConfigService service;

    public FeeFundConfigController(FeeFundConfigService service) {
        this.service = service;
    }

    @GetMapping("/config")
    public FeeFundConfigResponseDTO getConfig() {
        return service.getConfig();
    }
}
