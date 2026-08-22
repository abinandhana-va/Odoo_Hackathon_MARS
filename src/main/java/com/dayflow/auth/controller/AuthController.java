package com.dayflow.auth.controller;

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
 * AuthController — REST API for Authentication.
 *
 * <p><b>Base path:</b> /api/auth
 *
 * <p><b>Endpoints:</b>
 * <pre>
 *   POST /api/auth/register — register a new employee
 *   POST /api/auth/login    — login and receive a token
 * </pre>
 *
 * <p><b>First-commit stub:</b>
 * Both endpoints accept and validate the request body but return placeholder
 * messages. JWT token generation will be added in the next commit.
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
     * <p>Registers a new employee in the Dayflow system.
     * Expects a JSON body matching {@link RegisterRequest}.
     *
     * <p><b>Request body example:</b>
     * <pre>
     * {
     *   "name": "John Doe",
     *   "email": "john.doe@company.com",
     *   "password": "securePass123",
     *   "role": "EMPLOYEE"
     * }
     * </pre>
     *
     * @param request the validated registration payload
     * @return 201 Created with stub message (JWT response in future)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register — email={}", request.getEmail());
        String result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "STUB"));
    }

    /**
     * POST /api/auth/login
     *
     * <p>Authenticates an employee with email and password.
     * Expects a JSON body matching {@link LoginRequest}.
     *
     * <p><b>Request body example:</b>
     * <pre>
     * {
     *   "email": "john.doe@company.com",
     *   "password": "securePass123"
     * }
     * </pre>
     *
     * @param request the validated login payload
     * @return 200 OK with stub message (JWT token in future)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login — email={}", request.getEmail());
        String result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(result, "STUB"));
    }
}
