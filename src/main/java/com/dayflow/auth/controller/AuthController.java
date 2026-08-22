package com.dayflow.auth.controller;

import com.dayflow.auth.dto.AuthResponse;
import com.dayflow.auth.dto.LoginRequest;
import com.dayflow.auth.dto.RegisterRequest;
import com.dayflow.auth.service.AuthService;
import com.dayflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — REST API for Employee, HR, and Admin Authentication.
 *
 * <p><b>Endpoints:</b>
 * <pre>
 *   POST /api/auth/register — register a new employee / admin / HR
 *   POST /api/auth/login    — authenticate using email and password
 * </pre>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     *
     * <p>Registers a new Employee, Admin, or HR user in Dayflow.
     * Hashes password with BCrypt, enforces unique email and employee ID, and returns JWT.
     *
     * @param request the validated registration payload
     * @return 201 Created with AuthResponse (JWT + user info)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register — email={}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    /**
     * POST /api/auth/login
     *
     * <p>Authenticates a user with email and password.
     *
     * @param request the validated login payload
     * @return 200 OK with AuthResponse (JWT + user info)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login — email={}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
