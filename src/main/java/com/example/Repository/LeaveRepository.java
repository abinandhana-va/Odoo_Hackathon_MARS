package com.example.Repository;

import java.util.List;
import com.example.Model.LeaveRequest;
import com.example.Model.LeaveStatus;

public interface LeaveRepository {
    LeaveRequest save(LeaveRequest leaveRequest);
    LeaveRequest findById(Long id);
    List<LeaveRequest> findByEmployeeId(int employeeId);
    List<LeaveRequest> findAll();
    boolean updateStatus(Long id, LeaveStatus status, String adminComment);
}
