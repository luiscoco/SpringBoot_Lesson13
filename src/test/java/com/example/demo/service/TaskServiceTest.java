package com.example.demo.service;

import com.example.demo.Task;
import com.example.demo.TaskRepository;
import com.example.demo.dto.TaskDto;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskById_returnsDtoWithCorrectValues() {
        // Arrange
        long id = 1L;
        Task entity = new Task("Write tests", false);
        entity.setId(id);
        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        TaskDto result = taskService.getTaskById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.description()).isEqualTo("Write tests");
        assertThat(result.completed()).isFalse();
    }

    @Test
    void getTaskById_whenNotFound_throwsResourceNotFound() {
        // Arrange
        long id = 42L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> taskService.getTaskById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + id);
    }

    @Test
    void findAllTasks_mapsEntitiesToDtos() {
        // Arrange
        Task t1 = new Task("A", true); t1.setId(10L);
        Task t2 = new Task("B", false); t2.setId(11L);
        when(taskRepository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<TaskDto> result = taskService.findAllTasks();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(10L);
        assertThat(result.get(0).description()).isEqualTo("A");
        assertThat(result.get(0).completed()).isTrue();
        assertThat(result.get(1).id()).isEqualTo(11L);
        assertThat(result.get(1).description()).isEqualTo("B");
        assertThat(result.get(1).completed()).isFalse();
    }

    @Test
    void findTasksByStatus_delegatesToRepositoryAndMaps() {
        // Arrange
        Task t1 = new Task("Open1", false); t1.setId(1L);
        Task t2 = new Task("Open2", false); t2.setId(2L);
        when(taskRepository.findByCompleted(false)).thenReturn(List.of(t1, t2));

        // Act
        List<TaskDto> result = taskService.findTasksByStatus(false);

        // Assert
        assertThat(result).extracting(TaskDto::completed).containsOnly(false);
        assertThat(result).extracting(TaskDto::description).containsExactly("Open1", "Open2");
    }

    @Test
    void createTask_savesNewEntity_andReturnsDto() {
        // Arrange: incoming request object
        Task request = new Task("New Task", true);

        // Repository returns the saved entity with an id set
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task toSave = inv.getArgument(0, Task.class);
            Task saved = new Task(toSave.getDescription(), toSave.isCompleted());
            saved.setId(99L);
            return saved;
        });

        // Act
        TaskDto result = taskService.createTask(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.description()).isEqualTo("New Task");
        assertThat(result.completed()).isTrue();
    }
}

