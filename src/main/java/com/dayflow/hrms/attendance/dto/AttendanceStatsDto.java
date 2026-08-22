package com.dayflow.hrms.attendance.dto;

public class AttendanceStatsDto {
    private long workingDays;
    private long presentDays;
    private long absentDays;
    private long lateDays;
    private double attendancePercentage;

    public AttendanceStatsDto() {}

    public AttendanceStatsDto(long workingDays, long presentDays, long absentDays, long lateDays, double attendancePercentage) {
        this.workingDays = workingDays;
        this.presentDays = presentDays;
        this.absentDays = absentDays;
        this.lateDays = lateDays;
        this.attendancePercentage = attendancePercentage;
    }

    public long getWorkingDays() { return workingDays; }
    public void setWorkingDays(long workingDays) { this.workingDays = workingDays; }

    public long getPresentDays() { return presentDays; }
    public void setPresentDays(long presentDays) { this.presentDays = presentDays; }

    public long getAbsentDays() { return absentDays; }
    public void setAbsentDays(long absentDays) { this.absentDays = absentDays; }

    public long getLateDays() { return lateDays; }
    public void setLateDays(long lateDays) { this.lateDays = lateDays; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}
