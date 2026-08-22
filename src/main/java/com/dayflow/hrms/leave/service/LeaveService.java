package com.dayflow.hrms.leave.service;

import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.dto.LeaveSummaryDto;
import com.dayflow.hrms.leave.model.LeaveBalance;

import java.util.List;

public interface LeaveService {

    LeaveResponseDto applyLeave(LeaveApplicationRequestDto requestDto);

    List<LeaveResponseDto> getLeaveRequestsByEmployeeId(Long employeeId);

    LeaveResponseDto getLeaveRequestById(Long id);

    List<LeaveResponseDto> getAllLeaveRequests();

    List<LeaveResponseDto> getPendingLeaveRequests();

    LeaveResponseDto approveOrRejectLeave(Long leaveId, LeaveApprovalRequestDto approvalDto, String approverEmail);

    LeaveSummaryDto getLeaveSummaryByEmployeeId(Long employeeId);

    LeaveBalance getOrCreateLeaveBalance(Long employeeId);
}
