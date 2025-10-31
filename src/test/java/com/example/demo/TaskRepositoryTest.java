package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByCompleted_returnsMatchingTasks() {
        // Arrange: persist a completed task using TestEntityManager
        Task saved = new Task("Write repository slice test", true);
        entityManager.persist(saved);
        entityManager.flush();

        // Act: query repository by completed flag
        List<Task> results = taskRepository.findByCompleted(true);

        // Assert: the saved task is present and data matches
        assertThat(results).isNotEmpty();
        assertThat(results)
                .anySatisfy(t -> {
                    assertThat(t.getDescription()).isEqualTo("Write repository slice test");
                    assertThat(t.isCompleted()).isTrue();
                });
    }
}

