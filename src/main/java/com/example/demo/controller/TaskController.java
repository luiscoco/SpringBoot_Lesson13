package com.example.demo.controller;

import com.example.demo.Task;
import com.example.demo.dto.TaskDto;
import com.example.demo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<TaskDto> getTasks(@RequestParam(name = "completed", required = false) Boolean completed) {
        if (completed == null) {
            return taskService.findAllTasks();
        }
        return taskService.findTasksByStatus(completed);
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@RequestBody Task request) {
        return taskService.createTask(request);
    }

    @GetMapping("/tasks/{id}")
    public TaskDto getTaskById(@PathVariable long id) {
        return taskService.getTaskById(id);
    }
}
