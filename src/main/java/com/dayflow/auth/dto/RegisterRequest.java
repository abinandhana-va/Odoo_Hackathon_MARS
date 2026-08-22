package com.dayflow.auth.dto;

import com.dayflow.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest — Incoming payload for the POST /api/auth/register endpoint.
 *
 * <p>Used to register a new Employee, HR, or Admin account.
 */
public class RegisterRequest {

    /** Optional custom employee identifier (e.g. "EMP001"). If omitted, auto-generated. */
    private String employeeId;

    /** Full name of the new employee */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /** Corporate email — must be unique in the system */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    /** Plain-text password — will be hashed before storage */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** Role assigned to the new user (ADMIN, HR, EMPLOYEE) */
    @NotNull(message = "Role is required")
    private Role role;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public RegisterRequest(String employeeId, String name, String email, String password, Role role) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
