package com.dayflow.hrms.ai.service.impl;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.ai.dto.AiHrInsightsDto;
import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.leave.model.LeaveBalance;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.repository.LeaveBalanceRepository;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.payroll.model.Salary;
import com.dayflow.hrms.payroll.repository.PayrollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Production Data-Aware implementation of {@link AiAssistantService} for Dayflow HRMS.
 * Supports both direct repository access and mock-based delegation for Junit integration.
 */
@Service("hrmsAiAssistantService")
@Transactional(readOnly = true)
public class AiAssistantServiceImpl implements AiAssistantService {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRepository leaveRepository;
    private final JdbcTemplate jdbcTemplate;

    // Optional delegation fields used when instantiated by unit tests
    private final com.dayflow.hrms.leave.service.LeaveService leaveServiceField;
    private final com.dayflow.hrms.attendance.service.AttendanceService attendanceServiceField;
    private final com.dayflow.hrms.payroll.service.PayrollService payrollServiceField;

    // In-memory conversation history per employee session (keeps up to last 10 turns)
    private final Map<Long, List<String>> userSessionHistory = new ConcurrentHashMap<>();

    @Value("${dayflow.ai.system-prompt:You are Dayflow AI, an intelligent, empathetic, and professional HR assistant for Dayflow HRMS.}")
    private String systemPrompt;

    @Value("${dayflow.ai.api-key:}")
    private String apiKey;

    @Value("${dayflow.ai.model:gemini-1.5-flash}")
    private String modelName;

    /**
     * Spring Injection Constructor
     */
    @Autowired
    public AiAssistantServiceImpl(
            EmployeeRepository employeeRepository,
            @Qualifier("hrmsPayrollRepository") PayrollRepository payrollRepository,
            @Qualifier("hrmsLeaveBalanceRepository") LeaveBalanceRepository leaveBalanceRepository,
            LeaveRepository leaveRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRepository = leaveRepository;
        this.jdbcTemplate = jdbcTemplate;
        
        this.leaveServiceField = null;
        this.attendanceServiceField = null;
        this.payrollServiceField = null;
    }

    /**
     * Unit Test Mock Constructor
     */
    public AiAssistantServiceImpl(
            com.dayflow.hrms.leave.service.LeaveService leaveServiceField,
            EmployeeRepository employeeRepository,
            com.dayflow.hrms.attendance.service.AttendanceService attendanceServiceField,
            com.dayflow.hrms.payroll.service.PayrollService payrollServiceField) {
        this.employeeRepository = employeeRepository;
        this.leaveServiceField = leaveServiceField;
        this.attendanceServiceField = attendanceServiceField;
        this.payrollServiceField = payrollServiceField;

        this.payrollRepository = null;
        this.leaveBalanceRepository = null;
        this.leaveRepository = null;
        this.jdbcTemplate = null;
    }

    @Override
    public AiQueryResponseDto processQuery(AiQueryRequestDto request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            throw new BadRequestException("Prompt cannot be empty");
        }

        Long empId = request.getEmployeeId() != null ? request.getEmployeeId() : 1L;
        Employee employee = employeeRepository != null ? employeeRepository.findById(empId).orElse(null) : null;
        String prompt = request.getPrompt().trim();
        log.info("Processing Data-Aware AI HR query: '{}' for employee: {}", prompt, (employee != null ? employee.getEmail() : "Anonymous"));

        // 1. Privacy Guardrail: Detect attempts to probe another employee's private records
        if (violatesPrivacyPolicy(prompt, employee)) {
            return AiQueryResponseDto.builder()
                    .response("🔒 **Confidentiality & Privacy Policy Notice**:\n\n" +
                            "For privacy and security reasons, Dayflow HR Assistant cannot disclose personal, attendance, leave, or salary records of other employees. " +
                            "You are currently authorized only to access your own employee data.")
                    .intent("PRIVACY_BLOCKED")
                    .modelName("Dayflow-HR-AI-v1")
                    .suggestedActions(Arrays.asList("How many leaves do I have?", "What is my attendance this month?", "What is my salary?"))
                    .build();
        }

        // 2. Track in-memory session history
        if (employee != null) {
            List<String> history = userSessionHistory.computeIfAbsent(employee.getId(), k -> Collections.synchronizedList(new ArrayList<>()));
            if (history.size() >= 10) {
                history.remove(0);
            }
            history.add("User: " + prompt);
        }

        // 3. Process query using real backend HRMS data
        AiQueryResponseDto responseDto = answerDataAwareHrQuery(prompt, employee, empId);

        // Record assistant response in session history
        if (employee != null && responseDto != null && responseDto.getResponse() != null) {
            List<String> history = userSessionHistory.get(employee.getId());
            if (history != null) {
                if (history.size() >= 10) {
                    history.remove(0);
                }
                history.add("Assistant: " + responseDto.getResponse());
            }
        }

        return responseDto;
    }

    private boolean violatesPrivacyPolicy(String prompt, Employee currentEmployee) {
        String lower = prompt.toLowerCase();

        Pattern otherEmployeePattern = Pattern.compile("\\b(other employee|someone else|another employee|colleague's|manager's|all employees|everyone's)\\s+(salary|pay|id|details|leave|balance|attendance|check in|record)\\b");
        if (otherEmployeePattern.matcher(lower).find()) {
            return true;
        }

        if (currentEmployee != null) {
            String myName = (currentEmployee.getName() != null) ? currentEmployee.getName().toLowerCase() : "";
            if (lower.contains("salary of ") || lower.contains("pay of ") || lower.contains("leaves of ") || lower.contains("attendance of ")) {
                if (!myName.isEmpty() && !lower.contains(myName) && !lower.contains("my") && !lower.contains("i")) {
                    return true;
                }
            }
        }

        return false;
    }

    private AiQueryResponseDto answerDataAwareHrQuery(String prompt, Employee employee, Long empId) {
        String lower = prompt.toLowerCase();
        String responseText;
        String intent;
        List<String> actions = new ArrayList<>();

        String employeeName = employee != null ? employee.getName() : "Employee";

        // =====================================================================
        // Query 1: "How many leaves do I have?" / Leave Balance
        // =====================================================================
        if (lower.contains("how many leave") || lower.contains("leave balance") || lower.contains("my leaves") || lower.contains("leaves do i have") || lower.contains("vacation days")) {
            intent = "LEAVE_BALANCE";
            if (leaveBalanceRepository != null && employee != null) {
                Optional<LeaveBalance> balanceOpt = leaveBalanceRepository.findByEmployeeId(employee.getId());
                int paidBalance = balanceOpt.map(LeaveBalance::getPaidLeaveBalance).orElse(15);
                int sickBalance = balanceOpt.map(LeaveBalance::getSickLeaveBalance).orElse(10);
                int totalAvailable = paidBalance + sickBalance;

                responseText = String.format(
                        "🌴 **Your Live Leave Balance** (Employee: `%s`):\n\n" +
                        "• **Paid Leave (Annual)**: **%d days**\n" +
                        "• **Sick / Medical Leave**: **%d days**\n" +
                        "• **Total Available Leaves**: **%d days**\n\n" +
                        "You can submit a new leave request anytime from the **[Leave Portal](employee-leave-history.html)**.",
                        employee.getName(), paidBalance, sickBalance, totalAvailable
                );
            } else if (leaveServiceField != null) {
                com.dayflow.hrms.leave.dto.LeaveSummaryDto summary = leaveServiceField.getLeaveSummaryByEmployeeId(empId);
                responseText = "Dayflow AI: " + employeeName + ", you have "
                        + (summary != null ? summary.getPaidLeaveBalance() : 15) + " Paid Leave days and "
                        + (summary != null ? summary.getSickLeaveBalance() : 10) + " Sick Leave days remaining.";
            } else {
                responseText = "ℹ️ Please sign in to view your real-time leave balance. Standard annual allocations are **15 Paid Leaves** and **10 Sick Leaves**.";
            }
            actions.add("Do I have any pending leave requests?");
            actions.add("What is my attendance this month?");
            actions.add("What is my salary?");
        }

        // =====================================================================
        // Query 2: "Do I have any pending leave requests?" / Pending Leaves
        // =====================================================================
        else if (lower.contains("pending leave") || lower.contains("leave request") || lower.contains("status of my leave") || lower.contains("leave pending")) {
            intent = "PENDING_LEAVES";
            if (leaveRepository != null && employee != null) {
                List<LeaveRequest> pendingList = leaveRepository.findByEmployeeIdAndStatus(employee.getId(), LeaveStatus.PENDING);
                if (pendingList != null && !pendingList.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("📋 **You have %d pending leave request(s)**:\n\n", pendingList.size()));
                    for (LeaveRequest req : pendingList) {
                        sb.append(String.format(
                                "• **Request #%d** — `%s` Leave\n" +
                                "  Dates: `%s` to `%s` (%d days)\n" +
                                "  Reason: *%s*\n" +
                                "  Status: 🟡 **PENDING HR APPROVAL**\n\n",
                                req.getId(), req.getLeaveType(),
                                req.getStartDate(), req.getEndDate(), req.getTotalDays(),
                                (req.getReason() != null ? req.getReason() : "None provided")
                        ));
                    }
                    sb.append("You will be notified once HR reviews your request.");
                    responseText = sb.toString();
                } else {
                    responseText = "✅ **No Pending Leave Requests**:\n\n" +
                            "You currently do not have any leave applications awaiting approval. All previous requests have been processed.";
                }
            } else if (leaveServiceField != null) {
                com.dayflow.hrms.leave.dto.LeaveSummaryDto summary = leaveServiceField.getLeaveSummaryByEmployeeId(empId);
                responseText = "Dayflow AI: You have " + (summary != null ? summary.getPendingRequests() : 0) + " pending leave requests.";
            } else {
                responseText = "Please authenticate to view your pending leave applications.";
            }
            actions.add("How many leaves do I have?");
            actions.add("What is my attendance this month?");
            actions.add("What is my salary?");
        }

        // =====================================================================
        // Query 3: "What is my attendance this month?" / Monthly Attendance
        // =====================================================================
        else if (lower.contains("attendance this month") || lower.contains("my attendance") || lower.contains("attendance summary") || lower.contains("how many days was i present") || lower.contains("days present")) {
            intent = "MONTHLY_ATTENDANCE";
            if (employee != null && jdbcTemplate != null) {
                LocalDate now = LocalDate.now();
                int currentMonth = now.getMonthValue();
                int currentYear = now.getYear();
                String monthName = now.getMonth().name();

                int presentDays = 0;
                int halfDays = 0;
                int lateDays = 0;
                int totalLogs = 0;

                try {
                    String sql = "SELECT status FROM attendance WHERE employee_id = ? AND MONTH(attendance_date) = ? AND YEAR(attendance_date) = ?";
                    List<String> statuses = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("status"), employee.getId(), currentMonth, currentYear);
                    totalLogs = statuses.size();
                    for (String st : statuses) {
                        if ("PRESENT".equalsIgnoreCase(st)) presentDays++;
                        else if ("HALF_DAY".equalsIgnoreCase(st)) halfDays++;
                        else if ("LATE".equalsIgnoreCase(st)) lateDays++;
                    }
                } catch (Exception e) {
                    log.warn("Could not query monthly attendance table: {}", e.getMessage());
                }

                if (totalLogs > 0) {
                    responseText = String.format(
                            "⏱️ **Attendance Summary for %s %d** (Employee: `%s`):\n\n" +
                            "• **Total Recorded Shifts**: **%d days**\n" +
                            "• **Full Days Present**: **%d days**\n" +
                            "• **Half Days**: **%d days**\n" +
                            "• **Late Logins**: **%d days**\n\n" +
                            "You can view daily punch timestamps in the **[Attendance Portal](index.html)**.",
                            monthName, currentYear, employee.getName(),
                            totalLogs, presentDays, halfDays, lateDays
                    );
                } else {
                    responseText = String.format(
                            "⏱️ **Attendance Summary for %s %d**:\n\n" +
                            "No attendance punch records have been logged for your profile in %s %d yet. " +
                            "Make sure to click **Check In** in the **[Attendance Portal](index.html)** at the start of your workday!",
                            monthName, currentYear, monthName, currentYear
                    );
                }
            } else if (attendanceServiceField != null) {
                com.dayflow.hrms.attendance.dto.AttendanceStatsDto stats = attendanceServiceField.getAttendanceStats(empId);
                responseText = "Dayflow AI: Your attendance rate is " + (stats != null ? stats.getAttendancePercentage() : 100.0) + "%.";
            } else {
                responseText = "Please sign in to inspect your personal attendance logs for this month.";
            }
            actions.add("When did I last check in?");
            actions.add("How many leaves do I have?");
            actions.add("What is my salary?");
        }

        // =====================================================================
        // Query 4: "When did I last check in?" / Last Punch In
        // =====================================================================
        else if (lower.contains("last check in") || lower.contains("when did i check in") || lower.contains("last check-in") || lower.contains("punch in") || lower.contains("did i check in")) {
            intent = "LAST_CHECKIN";
            if (employee != null && jdbcTemplate != null) {
                String lastCheckInStr = null;
                String checkOutStr = null;
                String status = null;

                try {
                    String sql = "SELECT attendance_date, check_in_time, check_out_time, status FROM attendance WHERE employee_id = ? ORDER BY attendance_date DESC, id DESC LIMIT 1";
                    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, employee.getId());
                    if (!rows.isEmpty()) {
                        Map<String, Object> r = rows.get(0);
                        Object inObj = r.get("check_in_time");
                        Object outObj = r.get("check_out_time");
                        status = (String) r.get("status");
                        if (inObj != null) {
                            lastCheckInStr = inObj.toString();
                        }
                        if (outObj != null) {
                            checkOutStr = outObj.toString();
                        }
                    }
                } catch (Exception e) {
                    log.warn("Could not query last check-in: {}", e.getMessage());
                }

                if (lastCheckInStr != null) {
                    responseText = String.format(
                            "🚪 **Your Last Recorded Check-In**:\n\n" +
                            "• **Check-In Time**: `%s`\n" +
                            "• **Status**: `%s`\n" +
                            "• **Check-Out Time**: `%s`\n\n" +
                            "%s",
                            lastCheckInStr,
                            (status != null ? status : "PRESENT"),
                            (checkOutStr != null ? checkOutStr : "Not Checked Out Yet"),
                            (checkOutStr == null ? "💡 *Tip*: Don't forget to click **Check Out** on the Attendance Portal when completing your shift!" : "✅ Shift recorded.")
                    );
                } else {
                    responseText = "🚪 **No Check-In Record Found**:\n\n" +
                            "You have not logged any check-in punches yet. You can check in on the **[Attendance Portal](index.html)**.";
                }
            } else if (attendanceServiceField != null) {
                com.dayflow.hrms.attendance.dto.AttendanceResponseDto today = attendanceServiceField.getTodayAttendance(empId);
                String checkInStr = (today != null && today.getCheckInTime() != null) ? today.getCheckInTime().toLocalTime().toString() : "Not checked in yet";
                responseText = "Dayflow AI: Today's check-in status: " + checkInStr + ".";
            } else {
                responseText = "Please authenticate to view your last recorded punch-in time.";
            }
            actions.add("What is my attendance this month?");
            actions.add("How many leaves do I have?");
            actions.add("What is my salary?");
        }

        // =====================================================================
        // Query 5: "What is my salary?" / Salary & Compensation
        // =====================================================================
        else if (lower.contains("my salary") || lower.contains("what is my salary") || lower.contains("net pay") || lower.contains("my payslip") || lower.contains("allowance") || lower.contains("deduction") || lower.contains("compensation")) {
            intent = "SALARY_DATA";
            if (payrollRepository != null && employee != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("💵 **Your Compensation & Salary Details**:\n\n");
                List<Salary> salaries = payrollRepository.findByEmployeeId(employee.getId());
                if (salaries != null && !salaries.isEmpty()) {
                    Salary s = salaries.get(salaries.size() - 1);
                    sb.append(String.format(
                            "📊 **Latest Statement** (Pay Period: `%d/%d`):\n" +
                            "• **Basic Salary**: ₹%,.2f\n" +
                            "• **Allowances (+)**: +₹%,.2f\n" +
                            "• **Deductions (-)**: -₹%,.2f\n" +
                            "• **Net Salary (=)**: **₹%,.2f**\n" +
                            "• **Disbursement Status**: `%s`%s\n\n" +
                            "📐 **Calculation Formula**: $\\text{Net Salary} = \\text{Basic} + \\text{Allowances} - \\text{Deductions}$\n\n" +
                            "Full historical statements are available in the **[Payroll Dashboard](payroll-dashboard.html)**.",
                            s.getPayPeriodMonth(), s.getPayPeriodYear(),
                            s.getBasicSalary(),
                            s.getAllowances(),
                            s.getDeductions(),
                            s.getNetSalary(),
                            s.getPaymentStatus(),
                            (s.getPaymentDate() != null ? " (Paid on: " + s.getPaymentDate() + ")" : "")
                    ));
                } else {
                    sb.append("ℹ️ No finalized payroll statement is recorded for your profile yet. Your HR administrator will disburse your pay period slip.\n\n");
                }
                sb.append("🔒 *Note: Salary information is confidential and read-only for employees.*");
                responseText = sb.toString();
            } else if (payrollServiceField != null) {
                try {
                    List<com.dayflow.hrms.payroll.dto.PayrollResponseDto> payrolls = payrollServiceField.getPayrollByEmployeeId(empId);
                    if (payrolls != null && !payrolls.isEmpty()) {
                        com.dayflow.hrms.payroll.dto.PayrollResponseDto latest = payrolls.get(0);
                        responseText = "Dayflow AI: Your monthly Basic Salary is $" + latest.getBasicSalary()
                                + ", Allowances: $" + latest.getAllowances()
                                + ", Net Salary: $" + latest.getNetSalary() + ".";
                    } else {
                        responseText = "Dayflow AI: Base salary record is currently being processed by HR.";
                    }
                } catch(Exception e) {
                    responseText = "Dayflow AI: Base salary record is currently being processed by HR.";
                }
            } else {
                responseText = "Please sign in to view your confidential salary breakdown.";
            }

            actions.add("How many leaves do I have?");
            actions.add("What is my attendance this month?");
            actions.add("When did I check in?");
        }

        // =====================================================================
        // Query 6: Employee ID / Profile
        // =====================================================================
        else if (lower.contains("employee id") || lower.contains("my id") || lower.contains("who am i") || lower.contains("my profile") || lower.contains("my code") || lower.contains("employee information")) {
            intent = "EMPLOYEE_PROFILE";
            if (employee != null) {
                responseText = String.format(
                        "👤 **Your Employee Profile Information**:\n\n" +
                        "• **Employee Code**: `%s`\n" +
                        "• **Database ID**: `%d`\n" +
                        "• **Full Name**: %s\n" +
                        "• **Email**: %s\n" +
                        "• **Role**: %s\n" +
                        "• **Department**: %s\n\n" +
                        "Your Employee Code (`%s`) is your official unique identifier in Dayflow HRMS.",
                        employee.getEmployeeId(),
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getRole(),
                        employee.getDepartment() != null ? employee.getDepartment() : "General Department",
                        employee.getEmployeeId()
                );
            } else {
                responseText = "To view your Employee ID, please sign in with your corporate credentials.";
            }
            actions.add("How many leaves do I have?");
            actions.add("What is my salary?");
            actions.add("What is my attendance this month?");
        }

        // =====================================================================
        // General / Default HR Response
        // =====================================================================
        else {
            intent = "GENERAL_HR";
            responseText = String.format(
                    "👋 Hello %s! I am your **Dayflow AI HR Assistant** connected live to your HR data.\n\n" +
                    "I can answer live questions such as:\n" +
                    "• 🌴 **Leave Balance**: *\"How many leaves do I have?\"*\n" +
                    "• 📋 **Pending Requests**: *\"Do I have any pending leave requests?\"*\n" +
                    "• ⏱️ **Monthly Attendance**: *\"What is my attendance this month?\"*\n" +
                    "• 🚪 **Last Check-In**: *\"When did I last check in?\"*\n" +
                    "• 💵 **Salary Breakdown**: *\"What is my salary?\"*\n\n" +
                    "How may I assist you right now?",
                    employeeName
            );
            actions.add("How many leaves do I have?");
            actions.add("What is my attendance this month?");
            actions.add("When did I last check in?");
            actions.add("What is my salary?");
        }

        return AiQueryResponseDto.builder()
                .response(responseText)
                .intent(intent)
                .modelName("Dayflow-HR-AI-v1")
                .suggestedActions(actions)
                .build();
    }

    @Override
    public AiHrInsightsDto getHrInsights() {
        long employeeCount = employeeRepository != null ? employeeRepository.count() : 0L;
        long pendingLeaves = leaveRepository != null ? leaveRepository.findByStatus(LeaveStatus.PENDING).size() : 0L;

        int lateCount = 0;
        int presentCount = 0;
        int halfDayCount = 0;

        if (jdbcTemplate != null) {
            try {
                List<String> statuses = jdbcTemplate.query(
                        "SELECT status FROM attendance WHERE MONTH(attendance_date) = ? AND YEAR(attendance_date) = ?",
                        (rs, rowNum) -> rs.getString("status"),
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().getYear()
                );
                for (String st : statuses) {
                    if ("LATE".equalsIgnoreCase(st)) lateCount++;
                    else if ("PRESENT".equalsIgnoreCase(st)) presentCount++;
                    else if ("HALF_DAY".equalsIgnoreCase(st)) halfDayCount++;
                }
            } catch (Exception e) {
                log.warn("Error retrieving attendance for insights: {}", e.getMessage());
            }
        }

        BigDecimal totalPayroll = BigDecimal.ZERO;
        long salaryCount = 0;
        if (payrollRepository != null) {
            List<Salary> salaries = payrollRepository.findByPayPeriodMonthAndPayPeriodYear(
                    LocalDate.now().getMonthValue(), LocalDate.now().getYear()
            );
            salaryCount = salaries.size();
            for (Salary s : salaries) {
                totalPayroll = totalPayroll.add(s.getNetSalary() != null ? s.getNetSalary() : BigDecimal.ZERO);
            }
        }

        List<String> insightsList = Arrays.asList(
                String.format("Monthly payroll budget is ₹%,.2f across %d active salaries.", totalPayroll, salaryCount),
                String.format("Headcount is %d active employee profiles.", employeeCount),
                String.format("Recorded %d late check-ins and %d half-days this period.", lateCount, halfDayCount),
                String.format("There are currently %d pending leave requests.", pendingLeaves)
        );

        List<String> recommendations = Arrays.asList(
                pendingLeaves > 0 
                        ? String.format("Awaiting: Process the %d pending leave requests.", pendingLeaves)
                        : "All leave applications are up to date.",
                lateCount > 0
                        ? "Punctuality check: Review employee late warnings."
                        : "Employee check-ins are within optimal limits.",
                "Verify monthly salary disbursement before the upcoming deadline."
        );

        return new AiHrInsightsDto(
                "Dayflow HR Organizational Intelligence & Insights",
                "Automated analysis generated from real-time database logs for Attendance, Time Off, and Payroll.",
                insightsList,
                recommendations
        );
    }
}
