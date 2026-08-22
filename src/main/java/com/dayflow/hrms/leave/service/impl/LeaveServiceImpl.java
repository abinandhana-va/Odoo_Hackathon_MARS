package com.dayflow.hrms.leave.service.impl;

import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.dto.LeaveSummaryDto;
import com.dayflow.hrms.leave.model.LeaveBalance;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
import com.dayflow.hrms.leave.repository.LeaveBalanceRepository;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.leave.service.LeaveService;
import com.dayflow.hrms.notification.model.NotificationType;
import com.dayflow.hrms.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final NotificationService notificationService;

    public LeaveServiceImpl(LeaveRepository leaveRepository,
                            EmployeeRepository employeeRepository,
                            LeaveBalanceRepository leaveBalanceRepository,
                            NotificationService notificationService) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.notificationService = notificationService;
    }

    @Override
    public LeaveBalance getOrCreateLeaveBalance(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        return leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseGet(() -> {
                    LeaveBalance defaultBalance = new LeaveBalance(employee, 15, 10);
                    return leaveBalanceRepository.save(defaultBalance);
                });
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

        int requestedDays = (int) ChronoUnit.DAYS.between(requestDto.getStartDate(), requestDto.getEndDate()) + 1;

        // 1. Overlapping leave validation
        List<LeaveRequest> existingActiveRequests = leaveRepository.findByEmployeeIdAndStatusIn(
                employee.getId(), Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED)
        );

        for (LeaveRequest req : existingActiveRequests) {
            boolean isOverlapping = !(requestDto.getEndDate().isBefore(req.getStartDate()) || requestDto.getStartDate().isAfter(req.getEndDate()));
            if (isOverlapping) {
                throw new BadRequestException("Overlapping leave request detected: You already have a " + req.getStatus()
                        + " leave request from " + req.getStartDate() + " to " + req.getEndDate() + ".");
            }
        }

        // 2. Balance validation
        LeaveBalance balance = getOrCreateLeaveBalance(employee.getId());
        if (requestDto.getLeaveType() == LeaveType.PAID && balance.getPaidLeaveBalance() < requestedDays) {
            throw new BadRequestException("Insufficient Paid Leave balance. Requested: " + requestedDays + ", Available: " + balance.getPaidLeaveBalance());
        }
        if (requestDto.getLeaveType() == LeaveType.SICK && balance.getSickLeaveBalance() < requestedDays) {
            throw new BadRequestException("Insufficient Sick Leave balance. Requested: " + requestedDays + ", Available: " + balance.getSickLeaveBalance());
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(requestDto.getLeaveType());
        leaveRequest.setStartDate(requestDto.getStartDate());
        leaveRequest.setEndDate(requestDto.getEndDate());
        leaveRequest.setTotalDays(requestedDays);
        leaveRequest.setReason(requestDto.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRepository.save(leaveRequest);

        // Generate LEAVE_SUBMITTED notification
        String submittedMsg = "Your " + saved.getLeaveType() + " leave request (#" + saved.getId() + ") for "
                + saved.getStartDate() + " to " + saved.getEndDate() + " has been submitted and is pending HR approval.";
        notificationService.createNotification(employee, submittedMsg, NotificationType.LEAVE_SUBMITTED);

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
    @Transactional(readOnly = true)
    public List<LeaveResponseDto> searchLeaveRequests(Long employeeId, LeaveStatus status) {
        if (employeeId != null && status != null) {
            return leaveRepository.findByEmployeeIdAndStatus(employeeId, status)
                    .stream().map(LeaveResponseDto::fromEntity).collect(Collectors.toList());
        } else if (employeeId != null) {
            return getLeaveRequestsByEmployeeId(employeeId);
        } else if (status != null) {
            return leaveRepository.findByStatus(status)
                    .stream().map(LeaveResponseDto::fromEntity).collect(Collectors.toList());
        } else {
            return getAllLeaveRequests();
        }
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

        if (approvalDto.getStatus() == LeaveStatus.APPROVED) {
            // Deduct leave balance
            LeaveBalance balance = getOrCreateLeaveBalance(leaveRequest.getEmployee().getId());
            if (leaveRequest.getLeaveType() == LeaveType.PAID) {
                if (balance.getPaidLeaveBalance() < leaveRequest.getTotalDays()) {
                    throw new BadRequestException("Cannot approve: Insufficient Paid Leave balance. Requested: "
                            + leaveRequest.getTotalDays() + ", Available: " + balance.getPaidLeaveBalance());
                }
                balance.setPaidLeaveBalance(balance.getPaidLeaveBalance() - leaveRequest.getTotalDays());
            } else if (leaveRequest.getLeaveType() == LeaveType.SICK) {
                if (balance.getSickLeaveBalance() < leaveRequest.getTotalDays()) {
                    throw new BadRequestException("Cannot approve: Insufficient Sick Leave balance. Requested: "
                            + leaveRequest.getTotalDays() + ", Available: " + balance.getSickLeaveBalance());
                }
                balance.setSickLeaveBalance(balance.getSickLeaveBalance() - leaveRequest.getTotalDays());
            }
            leaveBalanceRepository.save(balance);

            // Low leave balance notification alert (<= 2 days)
            int remaining = (leaveRequest.getLeaveType() == LeaveType.PAID) ? balance.getPaidLeaveBalance() : balance.getSickLeaveBalance();
            if (leaveRequest.getLeaveType() != LeaveType.UNPAID && remaining <= 2) {
                String lowBalanceMsg = "Warning: Your remaining " + leaveRequest.getLeaveType() + " leave balance is low (" + remaining + " days remaining).";
                notificationService.createNotification(leaveRequest.getEmployee(), lowBalanceMsg, NotificationType.LOW_LEAVE_BALANCE);
            }
        }

        leaveRequest.setStatus(approvalDto.getStatus());
        leaveRequest.setAdminComment(approvalDto.getAdminComment());
        leaveRequest.setApprovedBy(approverEmail != null ? approverEmail : "HR_Admin");
        leaveRequest.setApprovedAt(LocalDateTime.now());

        LeaveRequest updated = leaveRepository.save(leaveRequest);

        // Generate Notification for Employee
        NotificationType notifType = (approvalDto.getStatus() == LeaveStatus.APPROVED)
                ? NotificationType.LEAVE_APPROVED
                : NotificationType.LEAVE_REJECTED;

        String commentSuffix = (approvalDto.getAdminComment() != null && !approvalDto.getAdminComment().isBlank())
                ? " HR Comment: " + approvalDto.getAdminComment()
                : "";

        String message = "Your " + leaveRequest.getLeaveType() + " leave request (#" + leaveRequest.getId() + ") for "
                + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate()
                + " has been " + approvalDto.getStatus().name() + "." + commentSuffix;

        notificationService.createNotification(leaveRequest.getEmployee(), message, notifType);

        return LeaveResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveSummaryDto getLeaveSummaryByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        LeaveBalance balance = getOrCreateLeaveBalance(employeeId);
        List<LeaveRequest> allRequests = leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);

        long total = allRequests.size();
        long pending = allRequests.stream().filter(r -> r.getStatus() == LeaveStatus.PENDING).count();
        long approved = allRequests.stream().filter(r -> r.getStatus() == LeaveStatus.APPROVED).count();
        long rejected = allRequests.stream().filter(r -> r.getStatus() == LeaveStatus.REJECTED).count();

        return new LeaveSummaryDto(
                employee.getId(),
                employee.getName(),
                balance.getPaidLeaveBalance(),
                balance.getSickLeaveBalance(),
                total,
                pending,
                approved,
                rejected
        );
    }
}
