package com.dayflow.employee.entity;

import com.dayflow.common.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Employee — The core shared entity of the Dayflow HRMS.
 *
 * <p><b>Used by all modules:</b>
 * <ul>
 *   <li>Auth module       — authentication/registration</li>
 *   <li>Attendance module — tracks employee attendance via {@code employeeId}</li>
 *   <li>Leave module      — manages employee leave requests</li>
 *   <li>Payroll module    — processes payslips for employees</li>
 * </ul>
 *
 * <p><b>Import path for other modules:</b>
 * {@code import com.dayflow.employee.entity.Employee;}
 */
@Entity
@Table(
    name = "employees",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_id",    columnNames = "employee_id"),
        @UniqueConstraint(name = "uk_employee_email", columnNames = "email")
    }
)
public class Employee {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable employee identifier, e.g. "EMP001".
     * Assigned at the time of registration.
     */
    @Column(name = "employee_id", nullable = false, unique = true, length = 20)
    private String employeeId;

    /** Full name of the employee */
    @Column(nullable = false, length = 100)
    private String name;

    /** Unique corporate email address — used as the login credential */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Hashed password — will be encoded with BCrypt in the Auth module.
     * Never expose this field in API responses (use EmployeeDto instead).
     */
    @Column(nullable = false)
    private String password;

    /** Role determines the access level across all modules */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /** Record creation timestamp — managed automatically by Hibernate */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Record last-updated timestamp — managed automatically by Hibernate */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Employee() {}

    public Employee(Long id, String employeeId, String name, String email,
                    String password, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.password = password;
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
        private String password;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Employee build() {
            return new Employee(id, employeeId, name, email, password, role, createdAt, updatedAt);
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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", employeeId='" + employeeId + "', name='" + name +
               "', email='" + email + "', role=" + role + "}";
    }
}
