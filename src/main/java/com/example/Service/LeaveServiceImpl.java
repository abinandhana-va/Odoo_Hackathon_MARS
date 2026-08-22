package com.example.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Model.LeaveRequest;
import com.example.Model.LeaveStatus;
import com.example.Repository.LeaveRepository;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;

    @Autowired
    public LeaveServiceImpl(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    @Override
    @Transactional
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) {
        // Enforce default status
        leaveRequest.setStatus(LeaveStatus.PENDING);
        return leaveRepository.save(leaveRequest);
    }

    @Override
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRepository.findById(id);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRepository.findAll();
    }

    @Override
    @Transactional
    public boolean approveOrRejectLeave(Long id, LeaveStatus status, String adminComment) {
        if (status == LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Cannot update status to PENDING. Must be APPROVED or REJECTED.");
        }
        return leaveRepository.updateStatus(id, status, adminComment);
    }
}
