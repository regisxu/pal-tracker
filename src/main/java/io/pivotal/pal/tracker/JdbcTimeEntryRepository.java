package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    DataSource dataSource;
    JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource){
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry any) {
        String sql = "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setLong(1, any.getProjectId());
                    ps.setLong(2, any.getUserId());
                    ps.setString(3, any.getDate().toString());
                    ps.setInt(4, any.getHours());
                    return ps;
                }, keyHolder);

        Number key = keyHolder.getKey();
        any.setId(key.longValue());
        return any;
    }

    @Override
    public TimeEntry find(long l) {
        String sql = "select * from time_entries where id="+l;
        try {
            return jdbcTemplate.queryForObject(sql, new RowMapper<TimeEntry>() {
                @Override
                public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimeEntry timeEntry = new TimeEntry();
                    timeEntry.setId(rs.getLong("id"));
                    timeEntry.setHours(rs.getInt("hours"));
                    timeEntry.setProjectId(rs.getLong("project_id"));
                    timeEntry.setUserId(rs.getLong("user_id"));
                    timeEntry.setDate(LocalDate.parse(rs.getString("date")));
                    return timeEntry;
                }
            });
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        String sql = "select * from time_entries";
        return jdbcTemplate.query(sql, new RowMapper<TimeEntry>() {
            @Override
            public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.setId(rs.getLong("id"));
                timeEntry.setHours(rs.getInt("hours"));
                timeEntry.setProjectId(rs.getLong("project_id"));
                timeEntry.setUserId(rs.getLong("user_id"));
                timeEntry.setDate(LocalDate.parse(rs.getString("date")));
                return timeEntry;
            }
        });
    }

    @Override
    public TimeEntry update(long eq, TimeEntry any) {
        String sql = "update time_entries set project_id=?,user_id=?,date=?, hours=? where id=?";

        int updated = jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setLong(1, any.getProjectId());
                    ps.setLong(2, any.getUserId());
                    ps.setString(3, any.getDate().toString());
                    ps.setInt(4, any.getHours());
                    ps.setLong(5, eq);
                    return ps;
                });

        any.setId(eq);
        return updated>0?any:null;
    }

    @Override
    public TimeEntry delete(long l) {
        TimeEntry timeEntry = find(l);
        if (timeEntry == null){
            return null;
        }
        jdbcTemplate.update("delete from time_entries where id="+l);
        return timeEntry;
    }

}
