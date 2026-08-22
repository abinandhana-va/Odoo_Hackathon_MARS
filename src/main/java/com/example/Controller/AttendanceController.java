package com.example.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.Model.Attendance;
import com.example.Service.AttendanceService;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Check-in API: POST http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/attendance/check-in?employeeId=1
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestParam int employeeId) {
        try {
            Attendance attendance = attendanceService.checkIn(employeeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing check-in: " + e.getMessage());
        }
    }

    // Check-out API: POST http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/attendance/check-out?employeeId=1
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@RequestParam int employeeId) {
        try {
            Attendance attendance = attendanceService.checkOut(employeeId);
            return ResponseEntity.ok(attendance);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing check-out: " + e.getMessage());
        }
    }

    // Retrieve today's attendance: GET http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/attendance/today?employeeId=1
    @GetMapping("/today")
    public ResponseEntity<?> getTodayAttendance(@RequestParam int employeeId) {
        try {
            Attendance attendance = attendanceService.getTodayAttendance(employeeId);
            if (attendance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No attendance log found for today for employee: " + employeeId);
            }
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching today's attendance: " + e.getMessage());
        }
    }
}
