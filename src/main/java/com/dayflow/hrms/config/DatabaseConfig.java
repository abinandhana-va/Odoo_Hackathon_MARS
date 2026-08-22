package com.dayflow.hrms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Central Database & JPA Configuration for Dayflow HRMS.
 * Compatible with H2 (development) and PostgreSQL / MySQL (production).
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
public class DatabaseConfig {
}
