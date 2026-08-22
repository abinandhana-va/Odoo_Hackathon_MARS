package com.dayflow.hrms.leave.repository;

import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    List<LeaveRequest> findByStatus(LeaveStatus status);
}
