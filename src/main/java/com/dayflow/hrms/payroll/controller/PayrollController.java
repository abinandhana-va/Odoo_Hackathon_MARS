package com.dayflow.hrms.payroll.controller;

import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.payroll.dto.PayrollRequestDto;
import com.dayflow.hrms.payroll.dto.PayrollResponseDto;
import com.dayflow.hrms.payroll.model.PaymentStatus;
import com.dayflow.hrms.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Dayflow HRMS Payroll operations.
 */
@RestController
@RequestMapping("/api/v1/payroll")
@Tag(name = "Payroll Management", description = "Endpoints for employee salary structures, calculations, and payroll processing")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping
    @Operation(summary = "Create or Update Payroll Record", description = "Creates or updates the payroll entry with automatic net salary calculation")
    public ResponseEntity<ApiResponse<PayrollResponseDto>> createOrUpdatePayroll(@Valid @RequestBody PayrollRequestDto requestDto) {
        PayrollResponseDto response = payrollService.createOrUpdatePayroll(requestDto);
        return new ResponseEntity<>(ApiResponse.ok("Payroll processed successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Payroll by ID", description = "Retrieves specific payroll details by payroll ID")
    public ResponseEntity<ApiResponse<PayrollResponseDto>> getPayrollById(@PathVariable Long id) {
        PayrollResponseDto response = payrollService.getPayrollById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get Payrolls by Employee ID", description = "Retrieves all payroll historical records for a given employee")
    public ResponseEntity<ApiResponse<List<PayrollResponseDto>>> getPayrollByEmployeeId(@PathVariable Long employeeId) {
        List<PayrollResponseDto> response = payrollService.getPayrollByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/period")
    @Operation(summary = "Get Payrolls by Pay Period", description = "Retrieves all payroll entries for a specific month and year")
    public ResponseEntity<ApiResponse<List<PayrollResponseDto>>> getPayrollByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<PayrollResponseDto> response = payrollService.getPayrollByPeriod(month, year);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @Operation(summary = "Get All Payroll Records", description = "Retrieves all payroll entries in the system")
    public ResponseEntity<ApiResponse<List<PayrollResponseDto>>> getAllPayrolls() {
        List<PayrollResponseDto> response = payrollService.getAllPayrolls();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Payment Status", description = "Updates payment status (PENDING, PROCESSED, PAID, CANCELLED)")
    public ResponseEntity<ApiResponse<PayrollResponseDto>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        PayrollResponseDto response = payrollService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Payment status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Payroll Record", description = "Deletes a specific payroll record")
    public ResponseEntity<ApiResponse<Void>> deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.ok(ApiResponse.ok("Payroll record deleted successfully", null));
    }
}
