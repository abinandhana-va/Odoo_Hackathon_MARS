package com.dayflow.hrms.leave.service.impl;

import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveServiceImpl(LeaveRepository leaveRepository, EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public LeaveResponseDto applyLeave(LeaveApplicationRequestDto requestDto) {
        if (requestDto == null) {
            throw new BadRequestException("Leave request cannot be empty");
        }

        if (requestDto.getStartDate() == null || requestDto.getEndDate() == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + requestDto.getEmployeeId()));

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(requestDto.getLeaveType());
        leaveRequest.setStartDate(requestDto.getStartDate());
        leaveRequest.setEndDate(requestDto.getEndDate());
        
        int calculatedDays = (int) ChronoUnit.DAYS.between(requestDto.getStartDate(), requestDto.getEndDate()) + 1;
        leaveRequest.setTotalDays(calculatedDays);
        leaveRequest.setReason(requestDto.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRepository.save(leaveRequest);
        return LeaveResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getLeaveRequestsByEmployeeId(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(LeaveResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveResponseDto getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return LeaveResponseDto.fromEntity(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getAllLeaveRequests() {
        return leaveRepository.findAll()
                .stream()
                .map(LeaveResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getPendingLeaveRequests() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING)
                .stream()
                .map(LeaveResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveResponseDto approveOrRejectLeave(Long leaveId, LeaveApprovalRequestDto approvalDto, String approverEmail) {
        if (approvalDto == null || approvalDto.getStatus() == null) {
            throw new BadRequestException("Approval status (APPROVED or REJECTED) is required");
        }

        if (approvalDto.getStatus() != LeaveStatus.APPROVED && approvalDto.getStatus() != LeaveStatus.REJECTED) {
            throw new BadRequestException("Status must be either APPROVED or REJECTED");
        }

        LeaveRequest leaveRequest = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        leaveRequest.setStatus(approvalDto.getStatus());
        leaveRequest.setAdminComment(approvalDto.getAdminComment());
        leaveRequest.setApprovedBy(approverEmail != null ? approverEmail : "HR_Admin");
        leaveRequest.setApprovedAt(LocalDateTime.now());

        LeaveRequest updated = leaveRepository.save(leaveRequest);
        return LeaveResponseDto.fromEntity(updated);
    }
}
