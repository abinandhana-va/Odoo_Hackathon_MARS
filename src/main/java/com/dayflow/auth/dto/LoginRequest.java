package com.dayflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest — Incoming payload for the POST /api/auth/login endpoint.
 *
 * <p>The Auth module will validate these fields before attempting authentication.
 * JWT token generation will be added in a future commit.
 */
public class LoginRequest {

    /** The employee's registered email address */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    /** The employee's plain-text password (will be BCrypt-verified) */
    @NotBlank(message = "Password is required")
    private String password;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
