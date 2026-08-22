package com.dayflow.hrms.leave.controller;

import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Employee Leave operations.
 */
@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "Leave Management", description = "Endpoints for employee leave application and leave history lookups")
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
    @Operation(summary = "Get Leave Requests by Employee", description = "Retrieves an employee's own leave request history")
    public ResponseEntity<ApiResponse<List<LeaveResponseDto>>> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        List<LeaveResponseDto> response = leaveService.getLeaveRequestsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
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
