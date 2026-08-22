package com.dayflow.hrms.payroll.repository;

import com.dayflow.hrms.payroll.model.PaymentStatus;
import com.dayflow.hrms.payroll.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("hrmsPayrollRepository")
public interface PayrollRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    List<Salary> findByPaymentStatus(PaymentStatus paymentStatus);
    Optional<Salary> findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(Long employeeId, Integer payPeriodMonth, Integer payPeriodYear);
    List<Salary> findByPayPeriodMonthAndPayPeriodYear(Integer payPeriodMonth, Integer payPeriodYear);
}
