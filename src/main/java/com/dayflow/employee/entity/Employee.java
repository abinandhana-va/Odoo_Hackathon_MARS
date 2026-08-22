package com.dayflow.employee.entity;

import com.dayflow.common.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "employees",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_id",    columnNames = "employee_id"),
        @UniqueConstraint(name = "uk_employee_email", columnNames = "email")
    }
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, unique = true, length = 20)
    private String employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // Personal Info
    @Column(length = 20)
    private String mobile;

    @Column(length = 255)
    private String address;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    // Job Info
    @Column(length = 100)
    private String company = "Dayflow Technologies";

    @Column(length = 100)
    private String department = "Engineering";

    @Column(name = "job_position", length = 100)
    private String jobPosition = "Software Engineer";

    @Column(length = 100)
    private String manager = "HR Manager";

    @Column(length = 100)
    private String location = "Headquarters";

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining = LocalDate.of(2024, 1, 15);

    // Additional Info
    @Column(length = 500)
    private String skills = "Java, Spring Boot, PostgreSQL, REST APIs, HTML/CSS";

    @Column(length = 500)
    private String certifications = "AWS Certified Developer, Certified Scrum Master";

    // Private Info
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth = LocalDate.of(1996, 5, 20);

    @Column(name = "residential_address", length = 255)
    private String residentialAddress;

    @Column(length = 50)
    private String nationality = "Indian";

    @Column(name = "personal_email", length = 150)
    private String personalEmail;

    @Column(length = 20)
    private String gender = "Male";

    @Column(name = "marital_status", length = 20)
    private String maritalStatus = "Single";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String employeeId;
        private String name;
        private String email;
        private String password;
        private Role role;
        private String mobile;
        private String address;
        private String department;
        private String jobPosition;
        private LocalDate dateOfJoining;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder phone(String phone) { this.mobile = phone; return this; }
        public Builder mobile(String mobile) { this.mobile = mobile; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder designation(String designation) { this.jobPosition = designation; return this; }
        public Builder jobPosition(String jobPosition) { this.jobPosition = jobPosition; return this; }
        public Builder joiningDate(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; return this; }
        public Builder status(String status) { return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Employee build() {
            Employee emp = new Employee(id, employeeId, name, email, password, role, createdAt, updatedAt);
            if (mobile != null) emp.setMobile(mobile);
            if (address != null) emp.setAddress(address);
            if (department != null) emp.setDepartment(department);
            if (jobPosition != null) emp.setJobPosition(jobPosition);
            if (dateOfJoining != null) emp.setDateOfJoining(dateOfJoining);
            return emp;
        }
    }

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

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPhone() { return mobile != null ? mobile : "9876543210"; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getJobPosition() { return jobPosition; }
    public void setJobPosition(String jobPosition) { this.jobPosition = jobPosition; }

    public String getDesignation() { return jobPosition != null ? jobPosition : "Software Engineer"; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    public LocalDate getJoiningDate() { return dateOfJoining != null ? dateOfJoining : LocalDate.of(2024, 1, 15); }

    public String getStatus() { return "ACTIVE"; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
