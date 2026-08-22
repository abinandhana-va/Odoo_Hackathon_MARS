package com.dayflow.auth;

import com.dayflow.auth.dto.LoginRequest;
import com.dayflow.auth.dto.RegisterRequest;
import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new employee and return JWT token")
    void testSuccessfulRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest("EMP001", "Alice Smith", "alice@dayflow.com", "password123", Role.EMPLOYEE);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email", is("alice@dayflow.com")))
                .andExpect(jsonPath("$.data.employeeId", is("EMP001")))
                .andExpect(jsonPath("$.data.role", is("EMPLOYEE")));

        // Verify entity saved in DB with BCrypt hashed password
        Employee saved = employeeRepository.findByEmail("alice@dayflow.com").orElseThrow();
        assertEquals("Alice Smith", saved.getName());
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));
        assertNotEquals("password123", saved.getPassword());
    }

    @Test
    @DisplayName("Should reject registration with duplicate email")
    void testDuplicateEmailRegistration() throws Exception {
        RegisterRequest request1 = new RegisterRequest("EMP001", "Bob Johnson", "bob@dayflow.com", "password123", Role.HR);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        RegisterRequest request2 = new RegisterRequest("EMP002", "Bob Duplicate", "bob@dayflow.com", "secret456", Role.HR);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Email is already registered")));
    }

    @Test
    @DisplayName("Should reject registration with duplicate employee ID")
    void testDuplicateEmployeeIdRegistration() throws Exception {
        RegisterRequest request1 = new RegisterRequest("EMP100", "Charlie Brown", "charlie@dayflow.com", "password123", Role.ADMIN);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        RegisterRequest request2 = new RegisterRequest("EMP100", "Charlie Two", "charlie2@dayflow.com", "password123", Role.EMPLOYEE);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Employee ID already exists")));
    }

    @Test
    @DisplayName("Should reject registration with invalid payload")
    void testInvalidRegistrationPayload() throws Exception {
        RegisterRequest request = new RegisterRequest("", "invalid-email", "123", null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testSuccessfulLogin() throws Exception {
        // Register user first
        RegisterRequest regRequest = new RegisterRequest("EMP005", "David Miller", "david@dayflow.com", "securePass123", Role.ADMIN);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        // Perform login
        LoginRequest loginRequest = new LoginRequest("david@dayflow.com", "securePass123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email", is("david@dayflow.com")))
                .andExpect(jsonPath("$.data.role", is("ADMIN")));
    }

    @Test
    @DisplayName("Should reject login with invalid password")
    void testLoginInvalidPassword() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("EMP006", "Eve Adams", "eve@dayflow.com", "correctPass", Role.EMPLOYEE);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("eve@dayflow.com", "wrongPass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid email or password")));
    }

    @Test
    @DisplayName("Should reject login with non-existent email")
    void testLoginNonExistentEmail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@dayflow.com", "somePass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid email or password")));
    }

    @Test
    @DisplayName("Should allow access to protected /api/employees/me with valid JWT token")
    void testProtectedEndpointWithJwtToken() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("EMP007", "Frank Wright", "frank@dayflow.com", "password123", Role.HR);
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = regResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).path("data").path("token").asText();

        // Access protected endpoint with Bearer token
        mockMvc.perform(get("/api/employees/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("frank@dayflow.com")))
                .andExpect(jsonPath("$.data.employeeId", is("EMP007")));
    }

    @Test
    @DisplayName("Should reject access to protected /api/employees/me without JWT token")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/employees/me"))
                .andExpect(status().isForbidden());
    }
}
