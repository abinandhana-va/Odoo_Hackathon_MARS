package com.dayflow.hrms.analytics.service.impl;

import com.dayflow.hrms.analytics.dto.AnalyticsSummaryDto;
import com.dayflow.hrms.analytics.dto.LeaveAnalyticsDto;
import com.dayflow.hrms.analytics.service.AnalyticsService;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final LeaveRepository leaveRepository;

    public AnalyticsServiceImpl(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    @Override
    public AnalyticsSummaryDto getPayrollAnalyticsSummary(Integer month, Integer year) {
        AnalyticsSummaryDto dto = new AnalyticsSummaryDto();
        dto.setTotalEmployees(0L);
        dto.setTotalMonthlyPayroll(BigDecimal.ZERO);
        dto.setAverageSalary(BigDecimal.ZERO);
        dto.setDepartmentHeadcount(Collections.emptyMap());
        dto.setStatusMessage("Analytics pipeline foundation initialized. Ready for advanced analytics logic.");
        return dto;
    }

    @Override
    public LeaveAnalyticsDto getLeaveAnalyticsSummary() {
        List<LeaveRequest> allRequests = leaveRepository.findAll();

        long total = allRequests.size();
        long pending = allRequests.stream().filter(r -> r.getStatus() == LeaveStatus.PENDING).count();
        long approved = allRequests.stream().filter(r -> r.getStatus() == LeaveStatus.APPROVED).count();
        long rejected = allRequests.stream().filter(r -> r.getStatus() == LeaveStatus.REJECTED).count();

        long paidCount = allRequests.stream().filter(r -> r.getLeaveType() == LeaveType.PAID).count();
        long sickCount = allRequests.stream().filter(r -> r.getLeaveType() == LeaveType.SICK).count();
        long unpaidCount = allRequests.stream().filter(r -> r.getLeaveType() == LeaveType.UNPAID).count();

        long decidedCount = approved + rejected;
        double approvalRate = decidedCount > 0 ? (double) approved / decidedCount * 100.0 : 0.0;

        return new LeaveAnalyticsDto(total, pending, approved, rejected, paidCount, sickCount, unpaidCount, Math.round(approvalRate * 100.0) / 100.0);
    }
}
