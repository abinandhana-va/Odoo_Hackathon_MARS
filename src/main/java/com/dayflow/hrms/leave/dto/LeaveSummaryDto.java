package com.dayflow.hrms.leave.dto;

public class LeaveSummaryDto {

    private Long employeeId;
    private String employeeName;
    private int paidLeaveBalance;
    private int sickLeaveBalance;
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;

    public LeaveSummaryDto() {
    }

    public LeaveSummaryDto(Long employeeId, String employeeName, int paidLeaveBalance, int sickLeaveBalance, long totalRequests, long pendingRequests, long approvedRequests, long rejectedRequests) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.paidLeaveBalance = paidLeaveBalance;
        this.sickLeaveBalance = sickLeaveBalance;
        this.totalRequests = totalRequests;
        this.pendingRequests = pendingRequests;
        this.approvedRequests = approvedRequests;
        this.rejectedRequests = rejectedRequests;
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public int getPaidLeaveBalance() { return paidLeaveBalance; }
    public void setPaidLeaveBalance(int paidLeaveBalance) { this.paidLeaveBalance = paidLeaveBalance; }

    public int getSickLeaveBalance() { return sickLeaveBalance; }
    public void setSickLeaveBalance(int sickLeaveBalance) { this.sickLeaveBalance = sickLeaveBalance; }

    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }

    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }

    public long getApprovedRequests() { return approvedRequests; }
    public void setApprovedRequests(long approvedRequests) { this.approvedRequests = approvedRequests; }

    public long getRejectedRequests() { return rejectedRequests; }
    public void setRejectedRequests(long rejectedRequests) { this.rejectedRequests = rejectedRequests; }
}
