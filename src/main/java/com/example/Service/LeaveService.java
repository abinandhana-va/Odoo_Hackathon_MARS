package com.example.Service;

import java.util.List;
import com.example.Model.LeaveRequest;
import com.example.Model.LeaveStatus;

public interface LeaveService {
    LeaveRequest applyForLeave(LeaveRequest leaveRequest);
    LeaveRequest getLeaveRequestById(Long id);
    List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId);
    List<LeaveRequest> getAllLeaveRequests();
    boolean approveOrRejectLeave(Long id, LeaveStatus status, String adminComment);
}
