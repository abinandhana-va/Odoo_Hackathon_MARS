package com.dayflow.hrms.leave.model;

import com.dayflow.employee.entity.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = "employee_id")
})
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee reference is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "paid_leave_balance", nullable = false)
    private Integer paidLeaveBalance = 15;

    @Column(name = "sick_leave_balance", nullable = false)
    private Integer sickLeaveBalance = 10;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LeaveBalance() {
    }

    public LeaveBalance(Employee employee, Integer paidLeaveBalance, Integer sickLeaveBalance) {
        this.employee = employee;
        this.paidLeaveBalance = paidLeaveBalance != null ? paidLeaveBalance : 15;
        this.sickLeaveBalance = sickLeaveBalance != null ? sickLeaveBalance : 10;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public Integer getPaidLeaveBalance() { return paidLeaveBalance; }
    public void setPaidLeaveBalance(Integer paidLeaveBalance) { this.paidLeaveBalance = paidLeaveBalance; }

    public Integer getSickLeaveBalance() { return sickLeaveBalance; }
    public void setSickLeaveBalance(Integer sickLeaveBalance) { this.sickLeaveBalance = sickLeaveBalance; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
