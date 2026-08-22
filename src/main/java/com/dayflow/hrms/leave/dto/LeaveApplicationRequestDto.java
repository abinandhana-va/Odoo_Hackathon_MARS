package com.dayflow.hrms.leave.dto;

import com.dayflow.hrms.leave.model.LeaveType;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload DTO for an employee applying for leave.
 * Flexibly accepts employeeId as numeric Long (1L), numeric string ("1"), or employee code ("EMP001").
 */
public class LeaveApplicationRequestDto {

    private Long employeeId;
    private String employeeCode;

    @NotNull(message = "Leave type is required (PAID, SICK, UNPAID)")
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 500, message = "Reason / remarks cannot exceed 500 characters")
    private String reason;

    public LeaveApplicationRequestDto() {
    }

    public LeaveApplicationRequestDto(Long employeeId, LeaveType leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    @JsonSetter("employeeId")
    public void setEmployeeId(Object val) {
        if (val == null) {
            this.employeeId = null;
            return;
        }
        if (val instanceof Number) {
            this.employeeId = ((Number) val).longValue();
        } else {
            String str = val.toString().trim();
            try {
                this.employeeId = Long.parseLong(str);
            } catch (NumberFormatException e) {
                this.employeeCode = str;
                this.employeeId = null;
            }
        }
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    @JsonSetter("employeeCode")
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
