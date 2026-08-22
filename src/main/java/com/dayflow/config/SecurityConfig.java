package com.dayflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig — Spring Security configuration for Dayflow HRMS.
 *
 * <p><b>First-commit strategy:</b>
 * All endpoints are permitted without authentication so that:
 * <ul>
 *   <li>The app compiles and starts successfully</li>
 *   <li>Attendance, Leave, and Payroll teams can develop without being blocked</li>
 *   <li>API endpoints can be tested with tools like Postman/curl without tokens</li>
 * </ul>
 *
 * <p><b>Planned for future commits:</b>
 * <ul>
 *   <li>JWT authentication filter</li>
 *   <li>Role-based access control per endpoint</li>
 *   <li>CORS configuration for frontend</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security filter chain — currently permits all requests.
     * JWT filter will be added here in the next commit.
     *
     * @param http the HttpSecurity builder
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless REST APIs
            .csrf(AbstractHttpConfigurer::disable)

            // Use stateless session (no server-side sessions — JWT will handle state)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Permit ALL requests for now — JWT filter will enforce auth in future
            .authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            );

        // TODO (future commit): Add JWT authentication filter here
        // http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCryptPasswordEncoder bean — available to AuthService for password hashing.
     * Registered here so it can be injected across the entire application context.
     *
     * @return BCryptPasswordEncoder with default strength (10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
