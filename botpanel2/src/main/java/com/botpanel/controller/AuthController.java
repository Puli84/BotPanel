package com.botpanel.controller;

import com.botpanel.dto.LoginRequest;
import com.botpanel.dto.LoginResponse;
import com.botpanel.dto.RegisterRequest;
import com.botpanel.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registrarSuperAdmin(request));
    }
}
