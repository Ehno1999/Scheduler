package com.example.demo.Task;

import com.example.demo.Scheduler.TaskSchedulerService;
import com.example.demo.Time.TimeUtilService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final JdbcTemplate jdbcTemplate;

    private final TaskRepository taskRepository;

    private final TimeUtilService timeUtilService;



    public TaskService(JdbcTemplate jdbcTemplate, TaskRepository taskRepository, TimeUtilService timeUtilService) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
        this.timeUtilService = timeUtilService;
    }

    @Transactional
    public String executeTask(Task task, TaskSchedulerService taskSchedulerService) {
        System.out.println(">>> executeTask() called for Task ID: " + task.getId());

        // Handle interval-based scheduling
        if (task.getinterval() > 0) {
            try {
                Date currentDate = timeUtilService.parseCustomDate(task.getDate());
                Date newDate = timeUtilService.addDays(currentDate, task.getinterval());  // Add days to the current task date

                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                String newDateStr = sdf.format(newDate);

                task.setDate(newDateStr);
                taskRepository.save(task);
                System.out.println("Updated task date: " + task.getDate());

            } catch (ParseException e) {
                System.err.println("Error parsing date for task ID " + task.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            deleteTask(task.getId());
        }

        // Reschedule the next task and execute the query
        String rescheduleMessage = taskSchedulerService.scheduleNextTask(taskSchedulerService);
        return executeQuery(task.getQuery()) + "\n" + rescheduleMessage;
    }

    @Transactional
    private void deleteTask(int taskId) {
        // Check if the task exists before attempting deletion
        if (!taskRepository.existsById(taskId)) {
            System.out.println("Task with ID " + taskId + " does not exist, skipping deletion.");
            return;
        }

        try {
            // Attempt to delete the task
            taskRepository.deleteById(taskId);
            System.out.println("Task deleted successfully: " + taskId);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Catch the optimistic locking failure exception
            System.err.println("Optimistic locking failure when deleting task with ID " + taskId + ": " + e.getMessage());
            // Optionally, log the error or take any other action, like retrying or notifying the user
        } catch (Exception e) {
            // Catch other general exceptions
            System.err.println("Error deleting task with ID " + taskId + ": " + e.getMessage());
        }
    }

    private String executeQuery(String query) {
        try {
            String queryType = query.trim().toUpperCase().split(" ")[0];
            // Get the first word in the query (e.g., SELECT, DELETE, UPDATE)
            switch (queryType) {
                case "SELECT":
                    return executeSelect(query);

                case "DELETE":
                    return executeDelete(query);

                case "UPDATE":
                    return executeUpdate(query);

                default:
                    return "Unsupported query type.";
            }

        } catch (Exception e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return "Error executing query: " + e.getMessage();
        }
    }

    private String executeSelect(String query) {
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            System.out.println("SELECT Query returned " + results.size() + " rows.");

            if (results.isEmpty()) {
                return "No results found.";
            }

            StringBuilder resultBuilder = new StringBuilder();
            for (Map<String, Object> row : results) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                resultBuilder.append("\n");
            }

            System.out.println("SELECT Query Results:\n" + resultBuilder.toString());
            return "Query executed successfully.";
        } catch (Exception e) {
            System.err.println("Error executing SELECT query: " + e.getMessage());
            e.printStackTrace();
            return "Error executing SELECT query: " + e.getMessage();
        }
    }



    private String executeDelete(String query) {
        try {
            // Attempt to execute the DELETE query
            int rowsAffected = jdbcTemplate.update(query);
            System.out.println("DELETE Query executed. Rows affected: " + rowsAffected);
            return rowsAffected + " rows deleted.";
        } catch (ObjectOptimisticLockingFailureException e) {
            // Handle the optimistic locking failure specifically
            System.err.println("Optimistic locking failure while executing DELETE query: " + e.getMessage());
            return "Optimistic locking failure while executing DELETE query: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Error executing DELETE query: " + e.getMessage());
            e.printStackTrace();
            return "Error executing DELETE query: " + e.getMessage();
        }
    }

    private String executeUpdate(String query) {
        try {
            int rowsAffected = jdbcTemplate.update(query);
            System.out.println("UPDATE Query executed. Rows affected: " + rowsAffected);
            return rowsAffected + " rows updated.";
        } catch (Exception e) {
            System.err.println("Error executing UPDATE query: " + e.getMessage());
            e.printStackTrace();
            return "Error executing UPDATE query: " + e.getMessage();
        }
    }
    public Task buildTask(String sqlQuery, String runTime) throws ParseException {
        Date parsedDate = timeUtilService.parseTimeForToday(runTime);
        String formattedDate = timeUtilService.formatToCustomDateFormat(parsedDate);
        Task newTask = new Task();
        newTask.setQuery(sqlQuery);
        newTask.setDate(formattedDate);

        return newTask;
    }
}
