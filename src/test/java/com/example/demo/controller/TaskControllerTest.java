package com.example.demo.controller;

import com.example.demo.dto.TaskDto;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc
@Import({TaskControllerTest.TestConfig.class, com.example.demo.config.SecurityConfig.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTaskById_returnsOkAndJsonBody() throws Exception {
        // Arrange
        long id = 1L;

        // Basic auth for in-memory user (user:password)
        String basicAuth = "Basic dXNlcjpwYXNzd29yZA==";

        // Act + Assert
        mockMvc.perform(get("/tasks/{id}", id)
                        .header("Authorization", basicAuth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Task"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        TaskService taskServiceStub() {
            return new TaskService(null) {
                @Override
                public TaskDto getTaskById(long id) {
                    return new TaskDto(1L, "Test Task", false);
                }
            };
        }
    }
}
