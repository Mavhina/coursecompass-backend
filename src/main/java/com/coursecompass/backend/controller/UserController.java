package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return new UserProfileResponse(user.getFullName());
    }
}
