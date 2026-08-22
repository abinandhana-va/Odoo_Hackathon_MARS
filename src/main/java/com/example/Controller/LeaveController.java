package com.example.Controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.Model.LeaveRequest;
import com.example.Model.LeaveStatus;
import com.example.Service.LeaveService;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    private final LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    // Apply for leave: POST http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/leave/request
    @PostMapping("/request")
    public ResponseEntity<?> applyForLeave(@Valid @RequestBody LeaveRequest leaveRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            LeaveRequest savedRequest = leaveService.applyForLeave(leaveRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting leave request: " + e.getMessage());
        }
    }

    // Get leaves for specific employee: GET http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/leave/employee/1
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getLeavesByEmployee(@PathVariable int employeeId) {
        List<LeaveRequest> requests = leaveService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(requests);
    }

    // Get all leave requests: GET http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/leave
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        List<LeaveRequest> requests = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(requests);
    }

    // Approve or Reject leave: POST http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/leave/approve
    @PostMapping("/approve")
    public ResponseEntity<String> approveOrRejectLeave(
            @RequestParam Long id,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String adminComment) {
        try {
            boolean updated = leaveService.approveOrRejectLeave(id, status, adminComment);
            if (updated) {
                return ResponseEntity.ok("Leave request status updated successfully to " + status);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found with ID: " + id);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing approval: " + e.getMessage());
        }
    }
}
