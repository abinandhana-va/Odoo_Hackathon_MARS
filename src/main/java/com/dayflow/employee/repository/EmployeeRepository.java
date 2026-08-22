package com.dayflow.employee.repository;

import com.dayflow.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * EmployeeRepository — Data access layer for the Employee entity.
 *
 * <p>Extends {@link JpaRepository} which automatically provides:
 * <ul>
 *   <li>findAll(), findById(), save(), delete(), count(), etc.</li>
 * </ul>
 *
 * <p><b>Custom finders below are used by:</b>
 * <ul>
 *   <li>Auth module    — {@code findByEmail} for login lookup</li>
 *   <li>HR module      — {@code findByEmployeeId} for employee lookup</li>
 *   <li>Other modules  — can inject this repository directly or use EmployeeService</li>
 * </ul>
 *
 * <p><b>Import path:</b>
 * {@code import com.dayflow.employee.repository.EmployeeRepository;}
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds an employee by their corporate email address.
     * Used primarily by the Auth module for login.
     *
     * @param email the employee's email
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Finds an employee by their human-readable employee ID (e.g. "EMP001").
     * Used by HR, Attendance, Leave, and Payroll modules.
     *
     * @param employeeId the employee ID string
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmployeeId(String employeeId);

    /**
     * Checks whether an email is already registered in the system.
     * Used during registration to prevent duplicates.
     *
     * @param email the email to check
     * @return true if an employee with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether an employeeId is already in use.
     * Used during employee creation to enforce uniqueness.
     *
     * @param employeeId the employeeId to check
     * @return true if the employee ID is taken
     */
    boolean existsByEmployeeId(String employeeId);
}
