package com.example.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.Model.Attendance;
import com.example.Model.AttendanceStatus;

@Repository
public class JdbcAttendanceRepository implements AttendanceRepository, InitializingBean {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAttendanceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initDatabase();
    }

    public void initDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS attendance (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "employee_id INT NOT NULL," +
                "attendance_date DATE NOT NULL," +
                "check_in_time TIMESTAMP," +
                "check_out_time TIMESTAMP," +
                "status VARCHAR(20) NOT NULL" +
                ")";
        try {
            jdbcTemplate.execute(createTableSql);
        } catch (Exception e) {
            System.err.println("Failed to initialize database table attendance: " + e.getMessage());
        }
    }

    private final RowMapper<Attendance> attendanceRowMapper = (rs, rowNum) -> {
        Attendance attendance = new Attendance();
        attendance.setId(rs.getLong("id"));
        attendance.setEmployeeId(rs.getInt("employee_id"));
        attendance.setDate(rs.getDate("attendance_date").toLocalDate());
        
        Timestamp inTs = rs.getTimestamp("check_in_time");
        if (inTs != null) {
            attendance.setCheckInTime(inTs.toLocalDateTime());
        }
        
        Timestamp outTs = rs.getTimestamp("check_out_time");
        if (outTs != null) {
            attendance.setCheckOutTime(outTs.toLocalDateTime());
        }
        
        attendance.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
        return attendance;
    };

    @Override
    public Attendance save(Attendance attendance) {
        if (attendance.getId() == null) {
            String insertSql = "INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, attendance.getEmployeeId());
                ps.setDate(2, Date.valueOf(attendance.getDate()));
                ps.setTimestamp(3, attendance.getCheckInTime() != null ? Timestamp.valueOf(attendance.getCheckInTime()) : null);
                ps.setTimestamp(4, attendance.getCheckOutTime() != null ? Timestamp.valueOf(attendance.getCheckOutTime()) : null);
                ps.setString(5, attendance.getStatus().name());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                attendance.setId(keyHolder.getKey().longValue());
            }
        } else {
            String updateSql = "UPDATE attendance SET employee_id = ?, attendance_date = ?, check_in_time = ?, check_out_time = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(updateSql,
                    attendance.getEmployeeId(),
                    Date.valueOf(attendance.getDate()),
                    attendance.getCheckInTime() != null ? Timestamp.valueOf(attendance.getCheckInTime()) : null,
                    attendance.getCheckOutTime() != null ? Timestamp.valueOf(attendance.getCheckOutTime()) : null,
                    attendance.getStatus().name(),
                    attendance.getId());
        }
        return attendance;
    }

    @Override
    public Attendance findByEmployeeIdAndDate(int employeeId, LocalDate date) {
        String sql = "SELECT * FROM attendance WHERE employee_id = ? AND attendance_date = ?";
        List<Attendance> list = jdbcTemplate.query(sql, attendanceRowMapper, employeeId, Date.valueOf(date));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean updateCheckOut(Long id, java.time.LocalDateTime checkOutTime, AttendanceStatus status) {
        String sql = "UPDATE attendance SET check_out_time = ?, status = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, Timestamp.valueOf(checkOutTime), status.name(), id);
        return updated > 0;
    }
}
