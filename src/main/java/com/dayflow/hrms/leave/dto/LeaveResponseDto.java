package com.dayflow.hrms.leave.dto;

import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Output DTO representing a leave request entry.
 */
public class LeaveResponseDto {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private LeaveStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LeaveResponseDto() {
    }

    public static LeaveResponseDto fromEntity(LeaveRequest leaveRequest) {
        LeaveResponseDto dto = new LeaveResponseDto();
        dto.setId(leaveRequest.getId());
        if (leaveRequest.getEmployee() != null) {
            dto.setEmployeeId(leaveRequest.getEmployee().getId());
            dto.setEmployeeCode(leaveRequest.getEmployee().getEmployeeCode());
            dto.setEmployeeName(leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName());
            dto.setDepartment(leaveRequest.getEmployee().getDepartment());
        }
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setTotalDays(leaveRequest.getTotalDays());
        dto.setStatus(leaveRequest.getStatus());
        dto.setReason(leaveRequest.getReason());
        dto.setCreatedAt(leaveRequest.getCreatedAt());
        dto.setUpdatedAt(leaveRequest.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
