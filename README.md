# Dayflow - AI-Powered Human Resource Management System (HRMS)

Dayflow is a modern, modular HRMS designed to streamline human resource operations, attendance tracking, leave management, payroll processing, and AI-driven organizational insights.

---

## Architecture & Module Allocation

This project is partitioned into cohesive domain modules to enable concurrent collaboration across team developers:

| Module | Responsible Developer | Scope & Features |
|---|---|---|
| **User & Employee Management** | **Person 1** | Authentication, roles, employee master profile, user credentials |
| **Attendance Management** | **Person 2** | Check-in/check-out logs, real-time presence, work hours |
| **Leave Management** | **Person 3** | Leave applications, approval workflows, leave balances |
| **Payroll, Analytics & AI** | **Person 4 (Current)** | Salary structures, net pay calculation, payroll disbursement, AI Assistant, HR metrics & reporting |

---

## Project Structure

```
src/main/java/com/dayflow/hrms/
├── DayflowApplication.java              # Spring Boot Application Entry Point
├── config/
│   ├── DatabaseConfig.java              # JPA Auditing & multi-database configuration
│   ├── OpenApiConfig.java               # Swagger / OpenAPI documentation
│   └── CorsConfig.java                  # CORS headers for web/mobile frontends
├── common/
│   ├── ApiResponse.java                 # Standard API response envelope
│   ├── GlobalExceptionHandler.java      # Centralized REST exception handler
│   └── exception/                       # Custom domain exceptions
├── employee/                            # Employee reference model & repository (Person 1 integration)
│   ├── model/Employee.java
│   └── repository/EmployeeRepository.java
├── attendance/                          # Person 2 Attendance module placeholder
│   └── package-info.java
├── leave/                               # Person 3 Leave module placeholder
│   └── package-info.java
├── payroll/                             # Payroll & Salary Processing (Active Module)
│   ├── model/
│   │   ├── Salary.java                  # Salary Entity (id, basicSalary, allowances, deductions, netSalary)
│   │   └── PaymentStatus.java           # Payment status enum
│   ├── dto/
│   │   ├── PayrollRequestDto.java       # Request payload validation
│   │   └── PayrollResponseDto.java      # Response payload with full breakdown
│   ├── repository/PayrollRepository.java# JPA queries for employee & period lookups
│   ├── service/                         # Payroll business logic & net salary computation
│   │   ├── PayrollService.java
│   │   └── impl/PayrollServiceImpl.java
│   └── controller/PayrollController.java# REST API endpoints (/api/v1/payroll)
├── analytics/                           # Analytics Module Placeholder
│   ├── dto/AnalyticsSummaryDto.java
│   ├── service/AnalyticsService.java
│   └── controller/AnalyticsController.java
├── ai/                                  # AI Assistant Module Placeholder
│   ├── dto/AiQueryRequestDto.java
│   ├── dto/AiQueryResponseDto.java
│   ├── service/AiAssistantService.java
│   └── controller/AiAssistantController.java
└── reports/                             # Reports Module Placeholder
    ├── dto/ReportRequestDto.java
    ├── service/ReportService.java
    └── controller/ReportController.java
```

---

## Payroll Module Details

### Salary Formula
$$\text{Net Salary} = \text{Basic Salary} + \text{Allowances} - \text{Deductions}$$

### Payroll REST Endpoints (`/api/v1/payroll`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/payroll` | Create or update employee payroll record |
| `GET` | `/api/v1/payroll/{id}` | Retrieve payroll record by ID |
| `GET` | `/api/v1/payroll/employee/{employeeId}` | Retrieve all payroll records for an employee |
| `GET` | `/api/v1/payroll/period?month=X&year=Y` | Retrieve payroll records for specific pay period |
| `GET` | `/api/v1/payroll` | Retrieve all payroll records in the system |
| `PATCH` | `/api/v1/payroll/{id}/status?status=PAID` | Update payroll disbursement status |
| `DELETE` | `/api/v1/payroll/{id}` | Delete payroll record |

---

## Building and Running

### Prerequisites
- **Java 21 LTS**
- **Maven 3.9+**

### Compile & Test
```bash
mvn clean compile
mvn test
```

### Run Application
```bash
mvn spring-boot:run
```

### API Documentation & Swagger UI
Once started, access interactive Swagger UI at:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:dayflowdb`)
