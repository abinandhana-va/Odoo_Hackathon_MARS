package com.dayflow.employee.dto;

import com.dayflow.common.enums.Role;
import java.time.LocalDateTime;

/**
 * EmployeeDto — Data Transfer Object for Employee API responses.
 *
 * <p>This DTO deliberately <b>excludes the password field</b> to ensure
 * password hashes are never sent in API responses.
 *
 * <p>Other modules (Attendance, Leave, Payroll) that need employee
 * information via REST should consume this DTO.
 */
public class EmployeeDto {

    /** Internal database primary key */
    private Long id;

    /** Human-readable identifier, e.g. "EMP001" */
    private String employeeId;

    /** Full name */
    private String name;

    /** Corporate email */
    private String email;

    /** Access level role */
    private Role role;

    /** Timestamp when the employee record was created */
    private LocalDateTime createdAt;

    /** Timestamp of last update */
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EmployeeDto() {}

    public EmployeeDto(Long id, String employeeId, String name, String email,
                       Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String employeeId;
        private String name;
        private String email;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public EmployeeDto build() {
            return new EmployeeDto(id, employeeId, name, email, role, createdAt, updatedAt);
        }
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
