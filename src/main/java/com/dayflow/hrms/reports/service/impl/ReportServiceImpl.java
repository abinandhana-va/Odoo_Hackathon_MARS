package com.dayflow.hrms.reports.service.impl;

import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.payroll.model.Salary;
import com.dayflow.hrms.payroll.repository.PayrollRepository;
import com.dayflow.hrms.reports.dto.ReportRequestDto;
import com.dayflow.hrms.reports.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Report Service Implementation — Compiles and exports detailed spreadsheet reports
 * in CSV format from actual database records.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final LeaveRepository leaveRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReportServiceImpl(
            EmployeeRepository employeeRepository,
            @Qualifier("hrmsPayrollRepository") PayrollRepository payrollRepository,
            LeaveRepository leaveRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
        this.leaveRepository = leaveRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String generateReport(ReportRequestDto request) {
        if (request == null || request.getReportType() == null) {
            throw new BadRequestException("Report type is required");
        }

        String type = request.getReportType().toUpperCase();
        log.info("Generating real CSV export report of type: {}", type);

        StringBuilder sb = new StringBuilder();

        switch (type) {
            case "HR_SUMMARY":
                sb.append("Employee ID,Full Name,Email,Role,Department,Designation,Phone,Joining Date,Status\n");
                List<Employee> employees = employeeRepository.findAll();
                for (Employee e : employees) {
                    sb.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                            escapeCsv(e.getEmployeeId()),
                            escapeCsv(e.getName()),
                            escapeCsv(e.getEmail()),
                            escapeCsv(e.getRole() != null ? e.getRole().name() : "EMPLOYEE"),
                            escapeCsv(e.getDepartment() != null ? e.getDepartment() : "General Department"),
                            escapeCsv(e.getDesignation() != null ? e.getDesignation() : "N/A"),
                            escapeCsv(e.getPhone() != null ? e.getPhone() : "N/A"),
                            e.getJoiningDate() != null ? e.getJoiningDate().toString() : "N/A",
                            escapeCsv(e.getStatus() != null ? e.getStatus() : "ACTIVE")
                    ));
                }
                break;

            case "MONTHLY_PAYROLL":
            case "PAYROLL":
                sb.append("Employee ID,Full Name,Pay Period,Basic Salary,Allowances,Deductions,Net Salary,Disbursement Status,Payment Date,Remarks\n");
                List<Salary> salaries = payrollRepository.findAll();
                for (Salary s : salaries) {
                    sb.append(String.format("\"%s\",\"%s\",\"%02d/%d\",%.2f,%.2f,%.2f,%.2f,\"%s\",\"%s\",\"%s\"\n",
                            escapeCsv(s.getEmployee() != null ? s.getEmployee().getEmployeeId() : "N/A"),
                            escapeCsv(s.getEmployee() != null ? s.getEmployee().getName() : "N/A"),
                            s.getPayPeriodMonth(), s.getPayPeriodYear(),
                            s.getBasicSalary(),
                            s.getAllowances(),
                            s.getDeductions(),
                            s.getNetSalary(),
                            s.getPaymentStatus() != null ? s.getPaymentStatus().name() : "PENDING",
                            s.getPaymentDate() != null ? s.getPaymentDate().toString() : "N/A",
                            escapeCsv(s.getRemarks() != null ? s.getRemarks() : "")
                    ));
                }
                break;

            case "ATTENDANCE":
                sb.append("Employee ID,Full Name,Attendance Date,Check In Time,Check Out Time,Shift Status\n");
                if (jdbcTemplate != null) {
                    try {
                        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                                "SELECT a.attendance_date, a.check_in_time, a.check_out_time, a.status, e.employee_id, e.name " +
                                "FROM attendance a JOIN employee e ON a.employee_id = e.id " +
                                "ORDER BY a.attendance_date DESC"
                        );
                        for (Map<String, Object> r : rows) {
                            sb.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                                    escapeCsv(r.get("employee_id") != null ? r.get("employee_id").toString() : ""),
                                    escapeCsv(r.get("name") != null ? r.get("name").toString() : ""),
                                    r.get("attendance_date") != null ? r.get("attendance_date").toString() : "",
                                    r.get("check_in_time") != null ? r.get("check_in_time").toString() : "N/A",
                                    r.get("check_out_time") != null ? r.get("check_out_time").toString() : "N/A",
                                    escapeCsv(r.get("status") != null ? r.get("status").toString() : "PRESENT")
                            ));
                        }
                    } catch (Exception e) {
                        log.error("Could not query attendance records for report: {}", e.getMessage());
                        sb.append("Error,Could not extract attendance log records from database\n");
                    }
                }
                break;

            case "LEAVE_SUMMARY":
            case "LEAVE":
                sb.append("Employee ID,Full Name,Leave Type,Start Date,End Date,Total Days,Status,Reason,Approved By,Remarks\n");
                List<LeaveRequest> requests = leaveRepository.findAll();
                for (LeaveRequest r : requests) {
                    sb.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                            escapeCsv(r.getEmployee() != null ? r.getEmployee().getEmployeeId() : "N/A"),
                            escapeCsv(r.getEmployee() != null ? r.getEmployee().getName() : "N/A"),
                            r.getLeaveType() != null ? r.getLeaveType().name() : "PAID",
                            r.getStartDate() != null ? r.getStartDate().toString() : "N/A",
                            r.getEndDate() != null ? r.getEndDate().toString() : "N/A",
                            r.getTotalDays(),
                            r.getStatus() != null ? r.getStatus().name() : "PENDING",
                            escapeCsv(r.getReason() != null ? r.getReason() : ""),
                            escapeCsv(r.getApprovedBy() != null ? r.getApprovedBy() : "N/A"),
                            escapeCsv(r.getAdminComment() != null ? r.getAdminComment() : "")
                    ));
                }
                break;

            default:
                throw new BadRequestException("Unsupported report type: " + type);
        }

        return sb.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) {
            return "";
        }
        return val.replace("\"", "\"\"");
    }
}
