package com.example.demo.Scheduler;

import com.example.demo.Task.Task;
import com.example.demo.Task.TaskRepository;
import com.example.demo.Task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SchedulerController {

    @Autowired
    private TaskScheduler taskScheduler;  // Injecting the TaskScheduler bean

    @Autowired
    private TaskService taskService;  // Injecting TaskService

    @Autowired
    private TaskRepository taskRepository;  // Injecting TaskRepository

    private String sqlQuery;

    // Method to schedule a query
    public String scheduleQuery(Task closestFutureTask) {
        try {
            // Format pattern that matches the string "Thu Mar 20 13:19:00 CET 2025"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

            // Parse the date string returned from taskService.getClosestFutureTask().getDate()
            Date scheduledTime = sdf.parse(closestFutureTask.getDate());  // Ensure we are using the task's date

            // Schedule the task using TaskScheduler
            taskScheduler.schedule(() -> runScheduledQuery(closestFutureTask), scheduledTime);  // Pass the query here
            return "Task scheduled for: " + scheduledTime.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing the time!";
        }
    }

    // Method that will be run by the scheduler and will call the taskService's doStuff method
    public void runScheduledQuery(Task closestFutureTask) {
        taskService.doStuff(closestFutureTask);
    }
}
