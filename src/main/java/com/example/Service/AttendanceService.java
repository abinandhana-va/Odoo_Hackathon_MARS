package com.example.Service;

import com.example.Model.Attendance;

public interface AttendanceService {
    Attendance checkIn(int employeeId);
    Attendance checkOut(int employeeId);
    Attendance getTodayAttendance(int employeeId);
}
