package com.example.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.Model.LeaveRequest;
import com.example.Model.LeaveStatus;
import com.example.Model.LeaveType;

@Repository
public class JdbcLeaveRepository implements LeaveRepository, InitializingBean {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcLeaveRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initDatabase();
    }

    public void initDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS leave_requests (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "employee_id INT NOT NULL," +
                "leave_type VARCHAR(20) NOT NULL," +
                "start_date DATE NOT NULL," +
                "end_date DATE NOT NULL," +
                "remarks VARCHAR(255)," +
                "status VARCHAR(20) NOT NULL," +
                "admin_comment VARCHAR(255)" +
                ")";
        try {
            jdbcTemplate.execute(createTableSql);
        } catch (Exception e) {
            // Log warning or print stack trace. In Spring MVC, we can print to stderr/stdout.
            System.err.println("Failed to initialize database table leave_requests: " + e.getMessage());
        }
    }

    private final RowMapper<LeaveRequest> leaveRequestRowMapper = (rs, rowNum) -> {
        LeaveRequest request = new LeaveRequest();
        request.setId(rs.getLong("id"));
        request.setEmployeeId(rs.getInt("employee_id"));
        request.setLeaveType(LeaveType.valueOf(rs.getString("leave_type")));
        request.setStartDate(rs.getDate("start_date").toLocalDate());
        request.setEndDate(rs.getDate("end_date").toLocalDate());
        request.setRemarks(rs.getString("remarks"));
        request.setStatus(LeaveStatus.valueOf(rs.getString("status")));
        request.setAdminComment(rs.getString("admin_comment"));
        return request;
    };

    @Override
    public LeaveRequest save(LeaveRequest leaveRequest) {
        if (leaveRequest.getId() == null) {
            String insertSql = "INSERT INTO leave_requests (employee_id, leave_type, start_date, end_date, remarks, status, admin_comment) VALUES (?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, leaveRequest.getEmployeeId());
                ps.setString(2, leaveRequest.getLeaveType().name());
                ps.setDate(3, Date.valueOf(leaveRequest.getStartDate()));
                ps.setDate(4, Date.valueOf(leaveRequest.getEndDate()));
                ps.setString(5, leaveRequest.getRemarks());
                ps.setString(6, leaveRequest.getStatus().name());
                ps.setString(7, leaveRequest.getAdminComment());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                leaveRequest.setId(keyHolder.getKey().longValue());
            }
        } else {
            String updateSql = "UPDATE leave_requests SET employee_id = ?, leave_type = ?, start_date = ?, end_date = ?, remarks = ?, status = ?, admin_comment = ? WHERE id = ?";
            jdbcTemplate.update(updateSql,
                    leaveRequest.getEmployeeId(),
                    leaveRequest.getLeaveType().name(),
                    Date.valueOf(leaveRequest.getStartDate()),
                    Date.valueOf(leaveRequest.getEndDate()),
                    leaveRequest.getRemarks(),
                    leaveRequest.getStatus().name(),
                    leaveRequest.getAdminComment(),
                    leaveRequest.getId());
        }
        return leaveRequest;
    }

    @Override
    public LeaveRequest findById(Long id) {
        String sql = "SELECT * FROM leave_requests WHERE id = ?";
        List<LeaveRequest> list = jdbcTemplate.query(sql, leaveRequestRowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<LeaveRequest> findByEmployeeId(int employeeId) {
        String sql = "SELECT * FROM leave_requests WHERE employee_id = ?";
        return jdbcTemplate.query(sql, leaveRequestRowMapper, employeeId);
    }

    @Override
    public List<LeaveRequest> findAll() {
        String sql = "SELECT * FROM leave_requests";
        return jdbcTemplate.query(sql, leaveRequestRowMapper);
    }

    @Override
    public boolean updateStatus(Long id, LeaveStatus status, String adminComment) {
        String sql = "UPDATE leave_requests SET status = ?, admin_comment = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, status.name(), adminComment, id);
        return updated > 0;
    }
}
