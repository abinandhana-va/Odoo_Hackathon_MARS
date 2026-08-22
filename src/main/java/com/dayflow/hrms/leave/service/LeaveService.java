package com.dayflow.hrms.leave.service;

import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;

import java.util.List;

public interface LeaveService {

    LeaveResponseDto applyLeave(LeaveApplicationRequestDto requestDto);

    List<LeaveResponseDto> getLeaveRequestsByEmployeeId(Long employeeId);

    LeaveResponseDto getLeaveRequestById(Long id);

    List<LeaveResponseDto> getAllLeaveRequests();
}
