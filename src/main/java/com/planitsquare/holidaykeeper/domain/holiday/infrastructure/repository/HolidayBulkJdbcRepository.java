package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository;

import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayBulkJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void bulkInsert(List<Holiday> holidays) {
        if (holidays.isEmpty()) return;
        String sql = "INSERT INTO holiday (country_id, date, local_name, name, fixed, global, launch_year, holiday_status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try {
            jdbcTemplate.batchUpdate(sql, holidays, 1000, (ps, h) -> {
                ps.setLong(1, h.getCountry().getId());
                ps.setObject(2, h.getDate());
                ps.setString(3, h.getLocalName());
                ps.setString(4, h.getName());
                ps.setBoolean(5, h.isFixed());
                ps.setBoolean(6, h.isGlobal());
                if (h.getLaunchYear() != null) ps.setInt(7, h.getLaunchYear());
                else ps.setNull(7, java.sql.Types.INTEGER);
                ps.setString(8, h.getHolidayStatus() != null ? h.getHolidayStatus().name() : null);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[HolidayBulkJdbcRepository] batch insert error: " + e.getMessage());
        }
    }
} 