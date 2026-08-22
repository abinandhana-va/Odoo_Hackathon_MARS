package com.dayflow.config;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.leave.model.LeaveBalance;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
import com.dayflow.hrms.leave.repository.LeaveBalanceRepository;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.payroll.model.PaymentStatus;
import com.dayflow.hrms.payroll.model.Salary;
import com.dayflow.hrms.payroll.repository.PayrollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DataLoader — Seeds H2 memory database with corporate profiles, leave balances,
 * sample request tickets, attendance clock punches, and payroll sheets.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRepository leaveRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataLoader(
            EmployeeRepository employeeRepository,
            @Qualifier("hrmsPayrollRepository") PayrollRepository payrollRepository,
            @Qualifier("hrmsLeaveBalanceRepository") LeaveBalanceRepository leaveBalanceRepository,
            LeaveRepository leaveRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRepository = leaveRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() > 0) {
            log.info("Database already contains records. Skipping DataLoader initialization.");
            return;
        }

        log.info("Starting corporate HRMS database seed initialization...");

        // 1. Create employees with hashed passwords
        String defaultHashedPassword = passwordEncoder.encode("password");

        Employee emp = new Employee.Builder()
                .employeeId("EMP001")
                .name("Manjari ArulJothi")
                .email("employee@dayflow.com")
                .password(defaultHashedPassword)
                .role(Role.EMPLOYEE)
                .phone("+91 98765 43210")
                .address("12, Nehru Street, Chennai, TN")
                .department("Engineering & Tech")
                .designation("Software Engineer")
                .joiningDate(LocalDate.of(2025, 1, 15))
                .status("ACTIVE")
                .build();
        emp = employeeRepository.save(emp);

        Employee hr = new Employee.Builder()
                .employeeId("EMP-HR001")
                .name("Manjari HR Manager")
                .email("hr@dayflow.com")
                .password(defaultHashedPassword)
                .role(Role.HR)
                .phone("+91 98765 43211")
                .address("45, Gandhi Road, Chennai, TN")
                .department("HR & Recruitment")
                .designation("HR Director")
                .joiningDate(LocalDate.of(2024, 6, 1))
                .status("ACTIVE")
                .build();
        hr = employeeRepository.save(hr);

        Employee admin = new Employee.Builder()
                .employeeId("EMP-ADM001")
                .name("Admin Director")
                .email("admin@dayflow.com")
                .password(defaultHashedPassword)
                .role(Role.ADMIN)
                .phone("+91 98765 43212")
                .address("90, Mount Road, Chennai, TN")
                .department("Finance & Operations")
                .designation("General Director")
                .joiningDate(LocalDate.of(2023, 1, 1))
                .status("ACTIVE")
                .build();
        admin = employeeRepository.save(admin);

        log.info("Corporate profiles seeded: [employee@dayflow.com, hr@dayflow.com, admin@dayflow.com]");

        // 2. Create leave balances for Employee
        LeaveBalance balance = new LeaveBalance();
        balance.setEmployee(emp);
        balance.setPaidLeaveBalance(12);
        balance.setSickLeaveBalance(8);
        leaveBalanceRepository.save(balance);

        // 3. Create leave requests
        LeaveRequest req1 = new LeaveRequest();
        req1.setEmployee(emp);
        req1.setLeaveType(LeaveType.PAID);
        req1.setStartDate(LocalDate.now().plusDays(2));
        req1.setEndDate(LocalDate.now().plusDays(4));
        req1.setTotalDays(3);
        req1.setReason("Family function and relocation");
        req1.setStatus(LeaveStatus.PENDING);
        req1.setCreatedAt(LocalDateTime.now());
        leaveRepository.save(req1);

        LeaveRequest req2 = new LeaveRequest();
        req2.setEmployee(emp);
        req2.setLeaveType(LeaveType.SICK);
        req2.setStartDate(LocalDate.now().minusDays(10));
        req2.setEndDate(LocalDate.now().minusDays(9));
        req2.setTotalDays(2);
        req2.setReason("Medical checkup and viral fever recovery");
        req2.setStatus(LeaveStatus.APPROVED);
        req2.setAdminComment("Approved on medical grounds.");
        req2.setApprovedBy(hr.getName());
        req2.setCreatedAt(LocalDateTime.now().minusDays(12));
        leaveRepository.save(req2);

        log.info("Leave balances and requests seeded.");

        // 4. Create payroll statements
        Salary pay1 = new Salary();
        pay1.setEmployee(emp);
        pay1.setBasicSalary(BigDecimal.valueOf(60000.00));
        pay1.setAllowances(BigDecimal.valueOf(15000.00));
        pay1.setDeductions(BigDecimal.valueOf(5000.00));
        pay1.setPayPeriodMonth(LocalDate.now().getMonthValue());
        pay1.setPayPeriodYear(LocalDate.now().getYear());
        pay1.setPaymentStatus(PaymentStatus.PAID);
        pay1.setPaymentDate(LocalDate.now().minusDays(5));
        pay1.setRemarks("Monthly salary disbursement");
        payrollRepository.save(pay1);

        Salary pay2 = new Salary();
        pay2.setEmployee(emp);
        pay2.setBasicSalary(BigDecimal.valueOf(60000.00));
        pay2.setAllowances(BigDecimal.valueOf(12000.00));
        pay2.setDeductions(BigDecimal.valueOf(4500.00));
        pay2.setPayPeriodMonth(LocalDate.now().minusMonths(1).getMonthValue());
        pay2.setPayPeriodYear(LocalDate.now().minusMonths(1).getYear());
        pay2.setPaymentStatus(PaymentStatus.PAID);
        pay2.setPaymentDate(LocalDate.now().minusMonths(1).withDayOfMonth(28));
        pay2.setRemarks("Salary disbursement");
        payrollRepository.save(pay2);

        log.info("Payroll slips seeded.");

        // 5. Create attendance punches using JdbcTemplate
        LocalDate today = LocalDate.now();
        
        // Present Today
        insertAttendancePunch(emp.getId(), today.toString(), "09:15:00", "18:00:00", "PRESENT");
        // Late Yesterday
        insertAttendancePunch(emp.getId(), today.minusDays(1).toString(), "09:45:00", "18:15:00", "LATE");
        // Half-Day 2 days ago
        insertAttendancePunch(emp.getId(), today.minusDays(2).toString(), "09:05:00", "12:30:00", "HALF_DAY");
        // Present 3 days ago
        insertAttendancePunch(emp.getId(), today.minusDays(3).toString(), "09:10:00", "18:05:00", "PRESENT");
        // Present 4 days ago
        insertAttendancePunch(emp.getId(), today.minusDays(4).toString(), "08:55:00", "18:00:00", "PRESENT");

        log.info("Attendance records seeded into H2 via JdbcTemplate.");
        log.info("Dayflow HRMS seed completed successfully.");
    }

    private void insertAttendancePunch(Long empId, String date, String checkIn, String checkOut, String status) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status) VALUES (?, ?, ?, ?, ?)",
                    empId, date, checkIn, checkOut, status
            );
        } catch (Exception e) {
            log.warn("Could not insert attendance punch: {}", e.getMessage());
        }
    }
}
