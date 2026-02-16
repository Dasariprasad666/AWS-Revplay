package com.example.demo.controller;

import com.example.demo.dto.UserProfileDTO;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint: GET http://localhost:8080/api/users/me
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        // The Authentication object automatically holds the email from the valid JWT!
        String email = authentication.getName();

        UserProfileDTO profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }
}