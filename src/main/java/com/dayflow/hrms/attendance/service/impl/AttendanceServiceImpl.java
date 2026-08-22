package com.dayflow.hrms.attendance.service.impl;

import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.attendance.dto.AttendanceResponseDto;
import com.dayflow.hrms.attendance.dto.AttendanceStatsDto;
import com.dayflow.hrms.attendance.model.Attendance;
import com.dayflow.hrms.attendance.model.AttendanceStatus;
import com.dayflow.hrms.attendance.repository.AttendanceRepository;
import com.dayflow.hrms.attendance.service.AttendanceService;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public AttendanceResponseDto checkIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        LocalDate today = LocalDate.now();
        Optional<Attendance> existingOpt = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);

        if (existingOpt.isPresent() && existingOpt.get().getCheckInTime() != null) {
            throw new BadRequestException("Employee has already checked in today at " + existingOpt.get().getCheckInTime().toLocalTime());
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isLate = now.toLocalTime().isAfter(LocalTime.of(9, 30));

        Attendance attendance = existingOpt.orElseGet(() -> new Attendance());
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setCheckInTime(now);
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setLate(isLate);

        Attendance saved = attendanceRepository.save(attendance);
        return AttendanceResponseDto.fromEntity(saved);
    }

    @Override
    public AttendanceResponseDto checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new BadRequestException("No check-in record found for today. Please check in first."));

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Employee has already checked out today at " + attendance.getCheckOutTime().toLocalTime());
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        Attendance updated = attendanceRepository.save(attendance);
        return AttendanceResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDto getTodayAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .map(AttendanceResponseDto::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceHistory(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByDateDesc(employeeId)
                .stream().map(AttendanceResponseDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceStatsDto getAttendanceStats(Long employeeId) {
        List<Attendance> list = attendanceRepository.findByEmployeeIdOrderByDateDesc(employeeId);
        long workingDays = list.size() > 0 ? list.size() : 22; // Default monthly working days
        long presentDays = list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long lateDays = list.stream().filter(Attendance::isLate).count();
        long absentDays = list.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();

        double pct = workingDays > 0 ? (double) presentDays / workingDays * 100.0 : 100.0;
        return new AttendanceStatsDto(workingDays, presentDays, absentDays, lateDays, Math.round(pct * 10.0) / 10.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceByDate(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return attendanceRepository.findByDate(targetDate)
                .stream().map(AttendanceResponseDto::fromEntity).collect(Collectors.toList());
    }
}
