package com.dayflow.hrms.attendance.controller;

import com.dayflow.hrms.attendance.dto.AttendanceResponseDto;
import com.dayflow.hrms.attendance.dto.AttendanceStatsDto;
import com.dayflow.hrms.attendance.service.AttendanceService;
import com.dayflow.hrms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "HRMS Attendance", description = "Endpoints for employee check-in, check-out, and attendance logs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    @Operation(summary = "Employee Check-In")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> checkIn(@RequestParam Long employeeId) {
        AttendanceResponseDto response = attendanceService.checkIn(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Checked in successfully at " + response.getCheckInTime().toLocalTime(), response));
    }

    @PostMapping("/check-out")
    @Operation(summary = "Employee Check-Out")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> checkOut(@RequestParam Long employeeId) {
        AttendanceResponseDto response = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Checked out successfully at " + response.getCheckOutTime().toLocalTime(), response));
    }

    @GetMapping("/today/{employeeId}")
    @Operation(summary = "Get today's attendance status for an employee")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> getTodayAttendance(@PathVariable Long employeeId) {
        AttendanceResponseDto response = attendanceService.getTodayAttendance(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Today's attendance retrieved", response));
    }

    @GetMapping("/history/{employeeId}")
    @Operation(summary = "Get attendance log history for an employee")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getAttendanceHistory(@PathVariable Long employeeId) {
        List<AttendanceResponseDto> history = attendanceService.getAttendanceHistory(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Attendance history retrieved", history));
    }

    @GetMapping("/stats/{employeeId}")
    @Operation(summary = "Get attendance percentage and statistics for an employee")
    public ResponseEntity<ApiResponse<AttendanceStatsDto>> getAttendanceStats(@PathVariable Long employeeId) {
        AttendanceStatsDto stats = attendanceService.getAttendanceStats(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Attendance stats retrieved", stats));
    }

    @GetMapping("/all")
    @Operation(summary = "HR/Admin: Get all employee attendance for a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceResponseDto> list = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(ApiResponse.ok("Daily attendance list retrieved", list));
    }
}
