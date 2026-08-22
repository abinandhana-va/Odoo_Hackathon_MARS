package com.dayflow.hrms.reports;

import com.dayflow.employee.service.EmployeeService;
import com.dayflow.hrms.attendance.service.AttendanceService;
import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.leave.service.LeaveService;
import com.dayflow.hrms.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "HRMS Reports & Payslips", description = "Endpoints for generating exportable and printable employee, attendance, leave, and payroll reports")
public class ReportsController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final PayrollService payrollService;

    public ReportsController(EmployeeService employeeService,
                              AttendanceService attendanceService,
                              LeaveService leaveService,
                              PayrollService payrollService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
        this.payrollService = payrollService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get executive HRMS summary report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExecutiveSummaryReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("totalEmployees", employeeService.getAllEmployees().size());
        report.put("totalLeaveRequests", leaveService.getAllLeaveRequests().size());
        report.put("pendingLeaveRequests", leaveService.getPendingLeaveRequests().size());
        report.put("generatedAt", java.time.LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ok("Executive report summary generated", report));
    }
}
