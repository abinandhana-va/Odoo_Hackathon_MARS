package com.dayflow.hrms.ai.service.impl;

import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import com.dayflow.hrms.leave.dto.LeaveSummaryDto;
import com.dayflow.hrms.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AiAssistantServiceImpl implements AiAssistantService {

    private final LeaveService leaveService;
    private final EmployeeRepository employeeRepository;

    public AiAssistantServiceImpl(LeaveService leaveService, EmployeeRepository employeeRepository) {
        this.leaveService = leaveService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public AiQueryResponseDto processQuery(AiQueryRequestDto request) {
        if (request == null || request.getPrompt() == null) {
            return new AiQueryResponseDto("Please provide a valid question or query.");
        }

        String promptLower = request.getPrompt().toLowerCase();

        if (promptLower.contains("leave") || promptLower.contains("vacation") || promptLower.contains("sick") || promptLower.contains("balance")) {
            Long empId = 1L; // Default target employee id for query context
            Optional<Employee> empOpt = employeeRepository.findById(empId);

            if (empOpt.isPresent()) {
                LeaveSummaryDto summary = leaveService.getLeaveSummaryByEmployeeId(empId);
                String answer = "Dayflow AI Assistant: Employee " + summary.getEmployeeName() + " has "
                        + summary.getPaidLeaveBalance() + " Paid Leave days and "
                        + summary.getSickLeaveBalance() + " Sick Leave days remaining. "
                        + "Total applications submitted: " + summary.getTotalRequests() + " ("
                        + summary.getPendingRequests() + " Pending, "
                        + summary.getApprovedRequests() + " Approved, "
                        + summary.getRejectedRequests() + " Rejected).";
                return new AiQueryResponseDto(answer);
            }
        }

        String fallbackAnswer = "Dayflow AI Assistant: Query received: '" + request.getPrompt() 
                + "'. You can query leave balances, pending applications, or payroll statuses.";
        return new AiQueryResponseDto(fallbackAnswer);
    }
}
