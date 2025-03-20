package com.example.demo.Task;

import com.example.demo.Scheduler.TaskSchedulerService;
import com.example.demo.Time.TimeUtilService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskScheduler taskScheduler;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final TaskSchedulerService taskSchedulerService;
    private final TimeUtilService timeUtilService;

    public TaskController(TaskScheduler taskScheduler, TaskRepository taskRepository,
                          TaskService taskService, TaskSchedulerService taskSchedulerService,
                          TimeUtilService timeUtilService) {
        this.taskScheduler = taskScheduler;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.taskSchedulerService = taskSchedulerService;
        this.timeUtilService = timeUtilService;
    }

    // Get all tasks
    @GetMapping("/findall")
    private List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Update an existing task
    @PutMapping("/update/{id}")
    private String updateTask(@PathVariable Integer id,
                              @RequestParam String sqlQuery,
                              @RequestParam String runTime,
                              @RequestParam int interval) {
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

    // Delete a task by ID
    @DeleteMapping("/delete/{id}")
    private String deleteTask(@PathVariable Integer id) {
        taskRepository.deleteById(id);
        return "Task deleted successfully!";
    }

    // Create a new task and schedule it
    @PostMapping("/create")
    private String createTask(@RequestParam String sqlQuery,
                              @RequestParam(required = false) String runTime,
                              @RequestParam(required = false) Integer interval) {
        try {
            // If runTime is null or empty, set it to 1 day from now
            if (runTime == null || runTime.isEmpty()) {
                runTime = timeUtilService.getOneDayFromNow();
            } else {
                // Parse the provided runTime to check if it is in the past
                Date parsedRunTime = timeUtilService.parseTimeForToday(runTime);
                if (parsedRunTime.before(new Date())) {
                    runTime = timeUtilService.getOneDayFromNow();
                }
            }

            // If interval is null or less than 0, set it to 0
            if (interval == null || interval < 0) {
                interval = 0;
            }
            Task newTask = taskService.buildTask(sqlQuery, runTime);
            newTask.setinterval(interval);
            taskRepository.save(newTask);
            taskSchedulerService.scheduleNextTask(taskSchedulerService);  // Pass the taskSchedulerService here

            return "Task scheduled successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error scheduling task.";
        }
    }

}
