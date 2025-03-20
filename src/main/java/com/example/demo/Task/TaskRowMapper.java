package com.example.demo.Task;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {

    /**
     * Maps a row from the ResultSet to a Task object.
     * Converts database columns to the corresponding Task fields.
     */
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId((int) rs.getLong("task_id"));
        task.setDate(rs.getString("date"));
        task.setQuery(rs.getString("query"));
        task.setinterval(rs.getInt("task_interval"));
        return task;
    }
}