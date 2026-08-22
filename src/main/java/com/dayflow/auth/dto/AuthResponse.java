package com.dayflow.auth.dto;

import com.dayflow.common.enums.Role;

/**
 * AuthResponse — Returned upon successful authentication (login or register).
 * Contains the JWT access token and essential user information.
 */
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private Role role;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String employeeId, String name, String email, Role role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public AuthResponse(String token, String tokenType, Long id, String employeeId, String name, String email, Role role) {
        this.token = token;
        this.tokenType = tokenType;
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
