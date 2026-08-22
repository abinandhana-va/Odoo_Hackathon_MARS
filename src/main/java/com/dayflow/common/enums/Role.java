package com.dayflow.common.enums;

/**
 * Role — defines the access level of a user within Dayflow HRMS.
 *
 * <p>Used by:
 * <ul>
 *   <li>Authentication module  — to assign roles at registration</li>
 *   <li>Employee module        — stored on the Employee entity</li>
 *   <li>Attendance module      — to guard attendance management APIs</li>
 *   <li>Leave module           — to guard leave approval APIs</li>
 *   <li>Payroll module         — to guard payroll processing APIs</li>
 * </ul>
 */
public enum Role {

    /** Full system access — can manage all modules */
    ADMIN,

    /** HR staff — can manage employees, leaves, and view payroll */
    HR,

    /** Regular employee — can view own data, apply for leave, view payslips */
    EMPLOYEE
}
