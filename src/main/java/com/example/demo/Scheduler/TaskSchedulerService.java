package com.example.demo.Scheduler;

import com.example.demo.Task.Task;
import com.example.demo.Task.TaskRowMapper;
import com.example.demo.Task.TaskService;
import com.example.demo.Time.TimeUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerService.class);



    private final JdbcTemplate jdbcTemplate;
    private final TaskScheduler taskScheduler;
    private final TaskService taskService;
    private final TimeUtilService timeUtilService;

    public TaskSchedulerService(JdbcTemplate jdbcTemplate, TaskScheduler taskScheduler, TaskService taskService, TimeUtilService timeUtilService) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskScheduler = taskScheduler;
        this.taskService = taskService;
        this.timeUtilService = timeUtilService;

    }



    // Schedule a task at the given time
    private String scheduleTask(Task task) {
        try {
            // Use parseCustomDate for the full date format
            Date scheduledTime = timeUtilService.parseCustomDate(task.getDate());

            if (scheduledTime.before(new Date())) {
                return "Cannot schedule a task in the past!";
            }

            taskScheduler.schedule(() -> executeScheduledTask(task), scheduledTime);
            return "Task scheduled for: " + scheduledTime;

        } catch (Exception e) {
            logger.error("Error scheduling the task!", e);
            return "Error scheduling the task!";
        }
    }

    // Execute the task
    private void executeScheduledTask(Task task) {
        taskService.executeTask(task, this);
    }
    private Task findNextScheduledTask() {
        String sql = "SELECT * FROM task " +
                "WHERE PARSEDATETIME(date, 'EEE MMM dd HH:mm:ss z yyyy') >= NOW() " +
                "ORDER BY PARSEDATETIME(date, 'EEE MMM dd HH:mm:ss z yyyy') ASC " +
                "LIMIT 1";

        List<Task> tasks = jdbcTemplate.query(sql, new TaskRowMapper());
        return tasks.isEmpty() ? null : tasks.get(0);
    }

    // Schedule the next task (called after execution)
    public String scheduleNextTask(TaskSchedulerService taskSchedulerService) {
        Task nextTask = findNextScheduledTask();
        if (nextTask != null) {
            System.out.println("Next scheduled task: " + nextTask);
            taskSchedulerService.scheduleTask(nextTask);
            return "Task successfully rescheduled.";
        } else {
            System.out.println("No future tasks found.");
            return "No task to schedule.";
        }
    }
}
