package com.dayflow.hrms.analytics.dto;

public class LeaveAnalyticsDto {

    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long paidLeaveCount;
    private long sickLeaveCount;
    private long unpaidLeaveCount;
    private double approvalRatePercentage;

    public LeaveAnalyticsDto() {
    }

    public LeaveAnalyticsDto(long totalRequests, long pendingRequests, long approvedRequests, long rejectedRequests, long paidLeaveCount, long sickLeaveCount, long unpaidLeaveCount, double approvalRatePercentage) {
        this.totalRequests = totalRequests;
        this.pendingRequests = pendingRequests;
        this.approvedRequests = approvedRequests;
        this.rejectedRequests = rejectedRequests;
        this.paidLeaveCount = paidLeaveCount;
        this.sickLeaveCount = sickLeaveCount;
        this.unpaidLeaveCount = unpaidLeaveCount;
        this.approvalRatePercentage = approvalRatePercentage;
    }

    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }

    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }

    public long getApprovedRequests() { return approvedRequests; }
    public void setApprovedRequests(long approvedRequests) { this.approvedRequests = approvedRequests; }

    public long getRejectedRequests() { return rejectedRequests; }
    public void setRejectedRequests(long rejectedRequests) { this.rejectedRequests = rejectedRequests; }

    public long getPaidLeaveCount() { return paidLeaveCount; }
    public void setPaidLeaveCount(long paidLeaveCount) { this.paidLeaveCount = paidLeaveCount; }

    public long getSickLeaveCount() { return sickLeaveCount; }
    public void setSickLeaveCount(long sickLeaveCount) { this.sickLeaveCount = sickLeaveCount; }

    public long getUnpaidLeaveCount() { return unpaidLeaveCount; }
    public void setUnpaidLeaveCount(long unpaidLeaveCount) { this.unpaidLeaveCount = unpaidLeaveCount; }

    public double getApprovalRatePercentage() { return approvalRatePercentage; }
    public void setApprovalRatePercentage(double approvalRatePercentage) { this.approvalRatePercentage = approvalRatePercentage; }
}
