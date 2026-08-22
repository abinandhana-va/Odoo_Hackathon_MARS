package com.dayflow.hrms.attendance.dto;

import com.dayflow.hrms.attendance.model.Attendance;
import com.dayflow.hrms.attendance.model.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceResponseDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private AttendanceStatus status;
    private boolean isLate;

    public AttendanceResponseDto() {}

    public static AttendanceResponseDto fromEntity(Attendance a) {
        AttendanceResponseDto dto = new AttendanceResponseDto();
        dto.setId(a.getId());
        if (a.getEmployee() != null) {
            dto.setEmployeeId(a.getEmployee().getId());
            dto.setEmployeeName(a.getEmployee().getName());
            dto.setEmployeeCode(a.getEmployee().getEmployeeId());
        }
        dto.setDate(a.getDate());
        dto.setCheckInTime(a.getCheckInTime());
        dto.setCheckOutTime(a.getCheckOutTime());
        dto.setStatus(a.getStatus());
        dto.setLate(a.isLate());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public boolean isLate() { return isLate; }
    public void setLate(boolean late) { isLate = late; }
}
