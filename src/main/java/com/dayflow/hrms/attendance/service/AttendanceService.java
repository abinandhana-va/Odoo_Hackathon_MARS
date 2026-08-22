package com.dayflow.hrms.attendance.service;

import com.dayflow.hrms.attendance.dto.AttendanceResponseDto;
import com.dayflow.hrms.attendance.dto.AttendanceStatsDto;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponseDto checkIn(Long employeeId);
    AttendanceResponseDto checkOut(Long employeeId);
    AttendanceResponseDto getTodayAttendance(Long employeeId);
    List<AttendanceResponseDto> getAttendanceHistory(Long employeeId);
    AttendanceStatsDto getAttendanceStats(Long employeeId);
    List<AttendanceResponseDto> getAttendanceByDate(LocalDate date);
}
