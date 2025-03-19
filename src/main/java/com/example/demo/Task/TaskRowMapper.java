package com.example.demo.Task;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId((int) rs.getLong("task_id")); // Map task ID
        task.setDate(rs.getString("date")); // Map the date field as string
        task.setQuery(rs.getString("query")); // Map the query field
        task.setinterval(rs.getInt("task_interval")); // Correctly map the 'daily' field from the database
        return task;
    }
}