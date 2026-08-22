package com.dayflow.auth.service;

import com.dayflow.auth.dto.AuthResponse;
import com.dayflow.auth.dto.LoginRequest;
import com.dayflow.auth.dto.RegisterRequest;
import com.dayflow.config.JwtTokenProvider;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * AuthService — Business logic for Authentication (Registration, Login, Password Hashing, JWT).
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Registers a new employee, HR, or Admin account.
     * Performs duplicate email and employee ID checks, hashes password with BCrypt,
     * saves the employee, and generates a JWT token.
     *
     * @param request the registration payload
     * @return AuthResponse containing JWT token and basic user info
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration for email={}", request.getEmail());

        // 1. Validate duplicate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed — Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        // 2. Determine and validate employee ID
        String employeeId;
        if (StringUtils.hasText(request.getEmployeeId())) {
            employeeId = request.getEmployeeId().trim();
            if (employeeRepository.existsByEmployeeId(employeeId)) {
                log.warn("Registration failed — Employee ID already exists: {}", employeeId);
                throw new IllegalArgumentException("Employee ID already exists: " + employeeId);
            }
        } else {
            employeeId = generateUniqueEmployeeId();
        }

        // 3. Hash password using BCrypt
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Build and save Employee entity
        Employee employee = Employee.builder()
                .employeeId(employeeId)
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(encodedPassword)
                .role(request.getRole())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Successfully registered employee id={}, employeeId={}, email={}",
                savedEmployee.getId(), savedEmployee.getEmployeeId(), savedEmployee.getEmail());

        // 5. Generate JWT token
        String token = tokenProvider.generateToken(savedEmployee);

        return new AuthResponse(
                token,
                savedEmployee.getId(),
                savedEmployee.getEmployeeId(),
                savedEmployee.getName(),
                savedEmployee.getEmail(),
                savedEmployee.getRole()
        );
    }

    /**
     * Authenticates an employee by email and password.
     *
     * @param request the login payload
     * @return AuthResponse containing JWT token and basic user info
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for email={}", request.getEmail());

        String email = request.getEmail().trim().toLowerCase();

        // 1. Lookup employee by email
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed — Email not found: {}", email);
                    return new BadCredentialsException("Invalid email or password");
                });

        // 2. Verify BCrypt password
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            log.warn("Login failed — Invalid password for email: {}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("Login successful for employee id={}, email={}", employee.getId(), employee.getEmail());

        // 3. Generate JWT token
        String token = tokenProvider.generateToken(employee);

        return new AuthResponse(
                token,
                employee.getId(),
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getRole()
        );
    }

    /**
     * Generates a unique employee ID formatted as EMP-XXXX (e.g. EMP-9B4F).
     */
    private String generateUniqueEmployeeId() {
        String candidate;
        do {
            String uuidPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
            candidate = "EMP-" + uuidPart;
        } while (employeeRepository.existsByEmployeeId(candidate));
        return candidate;
    }
}
