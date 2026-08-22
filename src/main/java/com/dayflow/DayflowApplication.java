package com.dayflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dayflow HRMS — Main Application Entry Point
 *
 * <p>Monorepo layout:
 * <ul>
 *   <li>com.dayflow.auth        — Authentication module</li>
 *   <li>com.dayflow.employee    — Employee management module (shared entity)</li>
 *   <li>com.dayflow.attendance  — Attendance module (owned by another developer)</li>
 *   <li>com.dayflow.leave       — Leave module (owned by another developer)</li>
 *   <li>com.dayflow.payroll     — Payroll module (owned by another developer)</li>
 *   <li>com.dayflow.common      — Shared utilities and enums</li>
 * </ul>
 */
@SpringBootApplication
public class DayflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(DayflowApplication.class, args);
    }
}
