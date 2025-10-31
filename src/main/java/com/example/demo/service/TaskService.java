package com.example.demo.service;

import com.example.demo.Task;
import com.example.demo.TaskRepository;
import com.example.demo.dto.TaskDto;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskDto> findAllTasks() {
        return toDtoList(taskRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> findTasksByStatus(boolean completed) {
        return toDtoList(taskRepository.findByCompleted(completed));
    }

    @Transactional
    public TaskDto createTask(Task request) {
        Task toSave = new Task(request.getDescription(), request.isCompleted());
        Task saved = taskRepository.save(toSave);
        return toDto(saved);
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(task.getId(), task.getDescription(), task.isCompleted());
    }

    private List<TaskDto> toDtoList(List<Task> tasks) {
        return tasks.stream().map(this::toDto).toList();
    }
}
