package com.example.Repository;

import java.time.LocalDate;
import com.example.Model.Attendance;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);
    Attendance findByEmployeeIdAndDate(int employeeId, LocalDate date);
    boolean updateCheckOut(Long id, java.time.LocalDateTime checkOutTime, com.example.Model.AttendanceStatus status);
}
