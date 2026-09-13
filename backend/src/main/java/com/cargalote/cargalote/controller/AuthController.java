package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (request.name() == null || request.name().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.user() == null || request.user().isBlank()
                || request.password() == null || request.password().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Completa todos los campos."));
        }

        try {
            String message = authService.register(
                    request.name().trim(),
                    request.email().trim().toLowerCase(),
                    request.user().trim().toLowerCase(),
                    request.password()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", message));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.user() == null || request.user().isBlank()
                || request.password() == null || request.password().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Completa usuario y contraseña."));
        }

        try {
            AuthService.User user = authService.login(
                    request.user().trim().toLowerCase(),
                    request.password()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "user", Map.of(
                                    "user", user.username(),
                                    "name", user.name(),
                                    "email", user.email(),
                                    "role", user.role()
                            )
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    public record RegisterRequest(
            String name,
            String email,
            String user,
            String password
    ) {
    }

    public record LoginRequest(
            String user,
            String password
    ) {
    }
}