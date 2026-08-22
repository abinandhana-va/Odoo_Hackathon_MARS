package com.dayflow.hrms.leave.controller;

import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.dto.LeaveSummaryDto;
import com.dayflow.hrms.leave.model.LeaveBalance;
import com.dayflow.hrms.leave.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "HRMS Leave Management", description = "Endpoints for leave applications, HR approval workflows, balance tracking, and summary statistics")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Employee submits a new leave application")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> applyLeave(@Valid @RequestBody LeaveApplicationRequestDto requestDto) {
        LeaveResponseDto response = leaveService.applyLeave(requestDto);
        return new ResponseEntity<>(ApiResponse.ok("Leave request submitted successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get leave application history for a specific employee")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        List<LeaveResponseDto> response = leaveService.getLeaveRequestsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Leave history retrieved successfully", response));
    }

    @GetMapping("/pending")
    @Operation(summary = "HR/Admin: View all pending leave requests requiring approval")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getPendingLeaveRequests() {
        List<LeaveResponseDto> pending = leaveService.getPendingLeaveRequests();
        return ResponseEntity.ok(ApiResponse.ok("Pending leave requests retrieved successfully", pending));
    }

    @PutMapping("/{id}/approval")
    @Operation(summary = "HR/Admin: Approve or reject a leave request with optional comments")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> approveOrRejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveApprovalRequestDto approvalDto,
            Principal principal) {
        String approverEmail = (principal != null) ? principal.getName() : "HR_Admin";
        LeaveResponseDto updated = leaveService.approveOrRejectLeave(id, approvalDto, approverEmail);
        return ResponseEntity.ok(ApiResponse.ok("Leave status updated to " + updated.getStatus(), updated));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "HR/Admin: Approve a leave request")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> approveLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String adminComment,
            Principal principal) {
        String approverEmail = (principal != null) ? principal.getName() : "HR_Admin";
        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(com.dayflow.hrms.leave.model.LeaveStatus.APPROVED, adminComment);
        LeaveResponseDto updated = leaveService.approveOrRejectLeave(id, approvalDto, approverEmail);
        return ResponseEntity.ok(ApiResponse.ok("Leave approved successfully", updated));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "HR/Admin: Reject a leave request")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> rejectLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String adminComment,
            Principal principal) {
        String approverEmail = (principal != null) ? principal.getName() : "HR_Admin";
        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(com.dayflow.hrms.leave.model.LeaveStatus.REJECTED, adminComment);
        LeaveResponseDto updated = leaveService.approveOrRejectLeave(id, approvalDto, approverEmail);
        return ResponseEntity.ok(ApiResponse.ok("Leave rejected successfully", updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get leave request details by ID")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> getLeaveRequestById(@PathVariable Long id) {
        LeaveResponseDto response = leaveService.getLeaveRequestById(id);
        return ResponseEntity.ok(ApiResponse.ok("Leave details retrieved successfully", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all leave requests across company")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getAllLeaveRequests() {
        List<LeaveResponseDto> response = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(ApiResponse.ok("All leave requests retrieved successfully", response));
    }

    @GetMapping("/summary/employee/{employeeId}")
    @Operation(summary = "Get leave statistics summary and current balances for an employee")
    public ResponseEntity<ApiResponse<LeaveSummaryDto>> getLeaveSummaryByEmployeeId(@PathVariable Long employeeId) {
        LeaveSummaryDto summary = leaveService.getLeaveSummaryByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Leave summary retrieved successfully", summary));
    }

    @GetMapping("/balance/employee/{employeeId}")
    @Operation(summary = "Get current leave balance for an employee")
    public ResponseEntity<ApiResponse<LeaveBalance>> getLeaveBalanceByEmployeeId(@PathVariable Long employeeId) {
        LeaveBalance balance = leaveService.getOrCreateLeaveBalance(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Leave balance retrieved successfully", balance));
    }
}
