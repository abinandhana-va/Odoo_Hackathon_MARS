package com.example.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Model.Attendance;
import com.example.Model.AttendanceStatus;
import com.example.Repository.AttendanceRepository;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 30);

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional
    public Attendance checkIn(int employeeId) {
        LocalDate today = LocalDate.now();
        
        // 1. Prevent duplicate check-in for the same employee on the same day.
        Attendance existing = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);
        if (existing != null) {
            throw new IllegalStateException("Employee has already checked in today.");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 2. Automatically determine status (LATE if checked in after 9:30 AM)
        AttendanceStatus status = AttendanceStatus.PRESENT;
        if (now.toLocalTime().isAfter(LATE_THRESHOLD)) {
            status = AttendanceStatus.LATE;
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(today);
        attendance.setCheckInTime(now);
        attendance.setStatus(status);

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public Attendance checkOut(int employeeId) {
        LocalDate today = LocalDate.now();
        
        // 3. Prevent check-out before check-in.
        Attendance existing = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);
        if (existing == null) {
            throw new IllegalStateException("Cannot check-out without a check-in for today.");
        }

        if (existing.getCheckOutTime() != null) {
            throw new IllegalStateException("Employee has already checked out today.");
        }

        LocalDateTime now = LocalDateTime.now();
        existing.setCheckOutTime(now);

        // 4. Automatically determine status based on duration of work (HALF_DAY if worked < 4 hours)
        AttendanceStatus finalStatus = existing.getStatus();
        if (existing.getCheckInTime() != null) {
            long workMinutes = Duration.between(existing.getCheckInTime(), now).toMinutes();
            if (workMinutes < 240) { // 4 hours = 240 minutes
                finalStatus = AttendanceStatus.HALF_DAY;
            }
        }
        existing.setStatus(finalStatus);

        attendanceRepository.save(existing);
        return existing;
    }

    @Override
    public Attendance getTodayAttendance(int employeeId) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, today);
    }
}
