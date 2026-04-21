package com.resumehelper.backend.auth.controller;

import com.resumehelper.backend.auth.dto.AuthResponse;
import com.resumehelper.backend.auth.dto.LoginRequest;
import com.resumehelper.backend.auth.dto.RegisterRequest;
import com.resumehelper.backend.auth.entity.User;
import com.resumehelper.backend.auth.repository.UserRepository;
import com.resumehelper.backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userRepository.findByEmail(userDetails.getUsername()).orElseThrow());
    }
}
