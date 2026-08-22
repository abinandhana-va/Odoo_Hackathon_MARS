package com.dayflow.hrms.leave.dto;

import com.dayflow.hrms.leave.model.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload DTO for HR/Admin leave approval or rejection.
 */
public class LeaveApprovalRequestDto {

    @NotNull(message = "Leave status is required (APPROVED or REJECTED)")
    private LeaveStatus status;

    @Size(max = 500, message = "Admin comment cannot exceed 500 characters")
    private String adminComment;

    public LeaveApprovalRequestDto() {
    }

    public LeaveApprovalRequestDto(LeaveStatus status, String adminComment) {
        this.status = status;
        this.adminComment = adminComment;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}
