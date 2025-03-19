package com.example.demo.Task;

import com.example.demo.Scheduler.SchedulerController;
import com.example.demo.Time.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SchedulerController schedulerController;

    @Autowired
    private TimeService timeService;

    public Task getClosestFutureTask() {
        String sql = "SELECT * FROM task " +
                "WHERE PARSEDATETIME(date, 'EEE MMM dd HH:mm:ss z yyyy') >= NOW() " +
                "ORDER BY PARSEDATETIME(date, 'EEE MMM dd HH:mm:ss z yyyy') ASC " +
                "LIMIT 1";

        List<Task> tasks = jdbcTemplate.query(sql, new TaskRowMapper());

        if (!tasks.isEmpty()) {
            Task task = tasks.get(0);
            System.out.println("Retrieved Task from DB: " + task); // Log the task from DB
            return task;
        }
        return null;
    }

    public String queTask() {
        // Fetch the updated task (latest task with the closest future date)
        Task updatedTask = getClosestFutureTask();
        if (updatedTask != null) {
            // Log the updated task to verify the date has been updated correctly
            System.out.println("Updated Task to queue: " + updatedTask.toString());

            // Schedule the task by passing the updated task to the scheduler
            String result = schedulerController.scheduleQuery(updatedTask);

            // Return result of scheduling (optional)
            return "Task successfully queued for the next day.";
        } else {
            // If no task is found to queue
            System.out.println("No task found to queue.");
            return "No task to queue.";
        }
    }

    @Transactional
    public String doStuff(Task closestFutureTask) {
        System.out.println(">>> doStuff() was called!");
        System.out.println("Executing doStuff() for Task ID: " + closestFutureTask.getId());
        System.out.println(closestFutureTask.getinterval());
        if (closestFutureTask.getinterval() > 0) {
            System.out.println("Task isDaily() is TRUE, proceeding to update the date...");
            try {
                // Parse the current task date (which is a string) into a Date object
                Date currentDate = timeService.parseCustomDate(closestFutureTask.getDate());

                // Add one day to the current date
                Date newDate = timeService.addOneDay(currentDate,closestFutureTask.getinterval());

                // Convert to string
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                String newDateStr = sdf.format(newDate);

                // Update task
                closestFutureTask.setDate(newDateStr);

                // Log before saving
                System.out.println("Before saving, task date: " + closestFutureTask.getDate());

                // Save the updated task
                taskRepository.save(closestFutureTask);

                // Log after saving
                System.out.println("After saving, task date: " + closestFutureTask.getDate());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            safeDeleteTask(closestFutureTask.getId());
        }

        // Queue the task for the next day
        String queuedMessage = queTask();

        // Run the query for the task
        return doquery(closestFutureTask.getQuery()) + "\n" + queuedMessage;
    }
    @Transactional
    public void safeDeleteTask(int taskId) {
        // Double-check the existence of the task before deleting
        if (!taskRepository.existsById(taskId)) {
            System.out.println("Task with ID " + taskId + " does not exist, skipping deletion.");
            return;
        }

        try {
            taskRepository.deleteById(taskId);
            System.out.println("Task deleted successfully: " + taskId);
        } catch (Exception e) {
            System.out.println("Error deleting task with ID " + taskId + ": " + e.getMessage());
        }
    }
    public String doquery(String query) {
        try {
            // Execute the SQL query and get the results as a list of maps
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);

            // Debugging: Check how many rows are returned
            System.out.println("Number of rows returned: " + results.size());

            // If there are no results
            if (results.isEmpty()) {
                System.out.println("No results found for the query.");
                return "No results found for the query.";
            }

            // Convert the results into a readable string format
            StringBuilder resultBuilder = new StringBuilder();
            for (Map<String, Object> row : results) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                resultBuilder.append("\n");  // Add a newline between rows
            }

            // Print the result to the console
            System.out.println("Query Results:\n" + resultBuilder.toString());
            return "Query executed successfully, results printed in the console.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error executing query: " + e.getMessage();
        }
    }
}
