package com.dayflow.employee.service;

import com.dayflow.employee.dto.EmployeeDto;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EmployeeService — Business logic layer for Employee management.
 *
 * <p>This is the <b>first-commit stub implementation</b>. CRUD operations
 * are wired up and functional. Advanced features (e.g. search, filters,
 * pagination) will be added in future commits.
 *
 * <p><b>Other modules that need employee data should inject this service</b>
 * rather than the repository directly, to preserve encapsulation.
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // =========================================================================
    // READ operations
    // =========================================================================

    /**
     * Returns all employees as DTOs (password excluded).
     *
     * @return list of all employees
     */
    public List<EmployeeDto> getAllEmployees() {
        log.debug("Fetching all employees");
        return employeeRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds an employee by their primary key.
     *
     * @param id the database id
     * @return Optional EmployeeDto
     */
    public Optional<EmployeeDto> getEmployeeById(Long id) {
        log.debug("Fetching employee by id={}", id);
        return employeeRepository.findById(id).map(this::toDto);
    }

    /**
     * Finds an employee by their human-readable employee ID (e.g. "EMP001").
     * Used by Attendance, Leave, and Payroll modules.
     *
     * @param employeeId the employee ID string
     * @return Optional EmployeeDto
     */
    public Optional<EmployeeDto> getEmployeeByEmployeeId(String employeeId) {
        log.debug("Fetching employee by employeeId={}", employeeId);
        return employeeRepository.findByEmployeeId(employeeId).map(this::toDto);
    }

    /**
     * Finds an employee by their email address.
     * Used primarily by the Auth module for login.
     *
     * @param email the employee's email
     * @return Optional Employee entity (NOT DTO — password needed for auth)
     */
    public Optional<Employee> getEmployeeEntityByEmail(String email) {
        log.debug("Fetching employee entity by email={}", email);
        return employeeRepository.findByEmail(email);
    }

    // =========================================================================
    // WRITE operations
    // =========================================================================

    /**
     * Persists a new employee record.
     *
     * <p><b>NOTE:</b> The caller (Auth module's register flow) is responsible
     * for encoding the password with BCrypt before passing the entity here.
     *
     * @param employee the fully populated Employee entity to save
     * @return saved Employee as a DTO
     */
    @Transactional
    public EmployeeDto createEmployee(Employee employee) {
        log.info("Creating new employee: email={}, employeeId={}", employee.getEmail(), employee.getEmployeeId());
        // TODO (future commit): Add duplicate-check logic
        Employee saved = employeeRepository.save(employee);
        return toDto(saved);
    }

    /**
     * Updates an existing employee's information.
     *
     * @param id             the id of the employee to update
     * @param updatedEmployee entity carrying updated values
     * @return Optional of the updated EmployeeDto, empty if not found
     */
    @Transactional
    public Optional<EmployeeDto> updateEmployee(Long id, Employee updatedEmployee) {
        log.info("Updating employee id={}", id);
        return employeeRepository.findById(id).map(existing -> {
            // TODO (future commit): Implement field-level patching
            existing.setName(updatedEmployee.getName());
            existing.setEmail(updatedEmployee.getEmail());
            existing.setRole(updatedEmployee.getRole());
            return toDto(employeeRepository.save(existing));
        });
    }

    /**
     * Deletes an employee by primary key.
     *
     * @param id the employee's database id
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deleteEmployee(Long id) {
        log.info("Deleting employee id={}", id);
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // =========================================================================
    // Mapper helpers
    // =========================================================================

    /**
     * Converts an Employee entity to a safe EmployeeDto (no password).
     *
     * @param employee the entity
     * @return EmployeeDto
     */
    public EmployeeDto toDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .name(employee.getName())
                .email(employee.getEmail())
                .role(employee.getRole())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
