package com.dayflow.auth.service;

import com.dayflow.auth.dto.LoginRequest;
import com.dayflow.auth.dto.RegisterRequest;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AuthService — Business logic for Authentication.
 *
 * <p>This is the <b>first-commit stub implementation</b>.
 * The structure is wired up so the Auth module compiles and is ready
 * for JWT integration in a future commit.
 *
 * <p><b>Planned for future commits:</b>
 * <ul>
 *   <li>BCrypt password encoding on register</li>
 *   <li>BCrypt password verification on login</li>
 *   <li>JWT token generation and return</li>
 *   <li>Refresh token support</li>
 * </ul>
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public AuthService(EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    /**
     * Registers a new employee in the Dayflow system.
     *
     * <p><b>Current stub behaviour:</b> Returns a placeholder message.
     * Full implementation (BCrypt + employeeId generation) will follow.
     *
     * @param request the registration payload
     * @return placeholder message string (will return JWT token in future)
     */
    public String register(RegisterRequest request) {
        log.info("Register request received for email={}", request.getEmail());

        // TODO (future commit): Check if email already exists
        // TODO (future commit): Encode password with BCryptPasswordEncoder
        // TODO (future commit): Generate unique employeeId (e.g. "EMP001")
        // TODO (future commit): Save employee and return JWT token

        return "Registration endpoint is ready. Full implementation coming in the next commit.";
    }

    /**
     * Authenticates an employee and returns a token.
     *
     * <p><b>Current stub behaviour:</b> Returns a placeholder message.
     * Full implementation (credential verification + JWT) will follow.
     *
     * @param request the login payload
     * @return placeholder message string (will return JWT token in future)
     */
    public String login(LoginRequest request) {
        log.info("Login request received for email={}", request.getEmail());

        // TODO (future commit): Load employee by email
        // TODO (future commit): Verify BCrypt password
        // TODO (future commit): Generate and return JWT token

        return "Login endpoint is ready. Full implementation coming in the next commit.";
    }
}
