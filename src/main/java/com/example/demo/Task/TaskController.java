package com.example.demo.Task;

import com.example.demo.Scheduler.SchedulerController;
import com.example.demo.Time.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private TaskRepository taskRepository;
    private String sqlQuery;

    @Autowired
    SchedulerController schedulerController;

    @Autowired
    private TaskService taskService;  // Add @Autowired here to inject TaskService

    @Autowired
    private TimeService timeService;

    @GetMapping("/findall")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    @PutMapping("/update/{id}")
    public String updateTask(@PathVariable Integer id, @RequestParam String sqlQuery, @RequestParam String runTime, @RequestParam int interval) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setQuery(sqlQuery);
            task.setDate(runTime);
            task.setinterval(interval);
            taskRepository.save(task);
            return "Task updated successfully!";
        }
        return "Task not found!";
    }

    // Delete task by ID
    @DeleteMapping("/delete/{id}")
    public String deleteTask(@PathVariable Integer id) {
        taskRepository.deleteById(id);
        return "Task deleted successfully!";
    }


    @PostMapping("/create")
    public String createTask(@RequestParam String sqlQuery, @RequestParam String runTime, @RequestParam int interval) {
        try {
            // Parse the received date in 'yyyy-MM-dd HH:mm:ss' format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = inputFormat.parse(runTime);

            // Convert the parsed Date to the desired format 'EEE MMM dd HH:mm:ss z yyyy'
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            String formattedDate = outputFormat.format(parsedDate);

            // Create and save new Task
            Task newTask = new Task();
            newTask.setQuery(sqlQuery);
            newTask.setDate(formattedDate);  // Save the formatted date
            newTask.setinterval(interval);
            taskRepository.save(newTask);

            // After saving the task, call queTask to schedule it
            taskService.queTask();  // Make sure this is called to schedule the task

            return "Task scheduled successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in scheduling task.";
        }
    }

    // A response wrapper for the created task
    public static class CreateTaskResponse {
        private String message;
        private Task task;

        public CreateTaskResponse(String message, Task task) {
            this.message = message;
            this.task = task;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }
}

