package com.dayflow.employee.controller;

import com.dayflow.common.response.ApiResponse;
import com.dayflow.employee.dto.EmployeeDto;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EmployeeController — REST API for Employee management.
 *
 * <p><b>Base path:</b> /api/employees
 *
 * <p><b>Endpoints:</b>
 * <pre>
 *   GET    /api/employees/me       — get profile of currently logged-in user (authenticated)
 *   GET    /api/employees          — list all employees (authenticated)
 *   GET    /api/employees/{id}     — get employee by id (authenticated)
 *   POST   /api/employees          — create employee (stub)
 *   PUT    /api/employees/{id}     — update employee (stub)
 *   DELETE /api/employees/{id}     — delete employee
 * </pre>
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * GET /api/employees/me
     * Returns the profile of the currently logged-in user identified by JWT token.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EmployeeDto>> getCurrentUser(@AuthenticationPrincipal Employee currentEmployee) {
        if (currentEmployee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User is not authenticated"));
        }
        log.debug("GET /api/employees/me — userId={}", currentEmployee.getId());
        EmployeeDto dto = employeeService.toDto(currentEmployee);
        return ResponseEntity.ok(ApiResponse.success("Current user profile retrieved", dto));
    }

    /**
     * GET /api/employees
     * Returns all employees (password excluded).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAllEmployees() {
        log.debug("GET /api/employees");
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", employees));
    }

    /**
     * GET /api/employees/{id}
     * Returns a single employee by database ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(@PathVariable Long id) {
        log.debug("GET /api/employees/{}", id);
        return employeeService.getEmployeeById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success("Employee found", dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found with id: " + id)));
    }

    /**
     * POST /api/employees
     * Creates a new employee record.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createEmployee() {
        log.info("POST /api/employees — stub endpoint");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Create employee endpoint is ready. Use POST /api/auth/register to create employees.",
                        "STUB"
                ));
    }

    /**
     * PUT /api/employees/{id}
     * Updates an existing employee.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateEmployee(@PathVariable Long id) {
        log.info("PUT /api/employees/{} — stub endpoint", id);
        return ResponseEntity.ok(
                ApiResponse.success("Update employee endpoint is ready — implementation coming soon.", "STUB")
        );
    }

    /**
     * DELETE /api/employees/{id}
     * Deletes an employee by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        log.info("DELETE /api/employees/{}", id);
        boolean deleted = employeeService.deleteEmployee(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Employee not found with id: " + id));
    }
}
