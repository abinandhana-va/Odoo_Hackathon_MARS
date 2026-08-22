package com.dayflow.hrms.leave.controller;

import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST API Controller for Employee Leave applications and HR Approval Workflow.
 */
@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "Leave Management", description = "Endpoints for employee leave application and HR approval workflow")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for Leave", description = "Submits a new leave request (PAID, SICK, UNPAID). Automatically defaults to PENDING status.")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> applyLeave(@Valid @RequestBody LeaveApplicationRequestDto requestDto) {
        LeaveResponseDto response = leaveService.applyLeave(requestDto);
        return new ResponseEntity<>(ApiResponse.ok("Leave request submitted successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get Leave Requests by Employee", description = "Retrieves an employee's own leave request history and updated statuses")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        List<LeaveResponseDto> response = leaveService.getLeaveRequestsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get Pending Leave Requests (HR/Admin)", description = "Retrieves all pending leave requests requiring HR approval")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getPendingLeaveRequests() {
        List<LeaveResponseDto> response = leaveService.getPendingLeaveRequests();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}/approval")
    @Operation(summary = "Approve or Reject Leave (HR/Admin)", description = "HR/Admin approves or rejects a leave request with optional comments")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> approveOrRejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveApprovalRequestDto approvalDto,
            Principal principal) {
        String approverEmail = (principal != null) ? principal.getName() : "HR_Admin";
        LeaveResponseDto response = leaveService.approveOrRejectLeave(id, approvalDto, approverEmail);
        return ResponseEntity.ok(ApiResponse.ok("Leave request status updated to " + approvalDto.getStatus(), response));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve Leave Request (HR/Admin)", description = "Shortcut endpoint to approve a leave request with optional remarks")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> approveLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String adminComment,
            Principal principal) {
        String approverEmail = (principal != null) ? principal.getName() : "HR_Admin";
        LeaveApprovalRequestDto dto = new LeaveApprovalRequestDto(LeaveStatus.APPROVED, adminComment);
        LeaveResponseDto response = leaveService.approveOrRejectLeave(id, dto, approverEmail);
        return ResponseEntity.ok(ApiResponse.ok("Leave request APPROVED successfully", response));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject Leave Request (HR/Admin)", description = "Shortcut endpoint to reject a leave request with optional remarks")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> rejectLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String adminComment,
            Principal principal) {
        String approverEmail = (principal != null) ? principal.getName() : "HR_Admin";
        LeaveApprovalRequestDto dto = new LeaveApprovalRequestDto(LeaveStatus.REJECTED, adminComment);
        LeaveResponseDto response = leaveService.approveOrRejectLeave(id, dto, approverEmail);
        return ResponseEntity.ok(ApiResponse.ok("Leave request REJECTED successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Leave Request Details", description = "Retrieves specific leave request by ID")
    public ResponseEntity<ApiResponse<LeaveResponseDto>> getLeaveRequestById(@PathVariable Long id) {
        LeaveResponseDto response = leaveService.getLeaveRequestById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @Operation(summary = "Get All Leave Requests", description = "Retrieves all leave requests in the system")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getAllLeaveRequests() {
        List<LeaveResponseDto> response = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
