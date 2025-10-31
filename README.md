# SpringBoot_Lesson9.2

## Propmt for the Code Agent (Codex, Gemini Code Assistant or Copilot)

**Context**:

I am learning to write integration tests for a Spring Boot REST API using Spring Boot 3.3 and Java 17.

I want to test the controller layer and HTTP responses using MockMvc, while mocking the service layer.

The project has Basic auth enabled: endpoint pattern /tasks/** requires role USER, and there is an in-memory user user with password password.

**Task**:

Generate a JUnit 5 integration test for a TaskController class.

**Known code in the app**:

Controller: com.example.demo.controller.TaskController

GET /tasks/{id} returns a TaskDto

GET /tasks supports optional completed query param

POST /tasks accepts a Task and returns a TaskDto (201 Created)

Service: com.example.demo.service.TaskService

Methods include TaskDto getTaskById(long id)

DTO: com.example.demo.dto.TaskDto (record: TaskDto(Long id, String description, boolean completed))

Security: Basic auth required for /tasks/**; in-memory user user / password.

**Constraints**:

Use @SpringBootTest to load the application context.

Use @AutoConfigureMockMvc to test the web layer without a real HTTP server.

Mock TaskService with @MockBean.

Use MockMvc to perform the request.

Include a Basic auth header for the request (user:password).

**Steps**:

Create src/test/java/com/example/demo/controller/TaskControllerTest.java with package com.example.demo.controller.

Annotate the test class with @SpringBootTest and @AutoConfigureMockMvc.

Inject MockMvc and declare @MockBean TaskService taskService.

Stub taskService.getTaskById(1L) to return new TaskDto(1L, "Test Task", false).

Perform a GET request to /tasks/1 with Basic auth for user:password.

Assert HTTP status 200 (OK).

Assert the response is JSON and matches the DTO: id = 1, description = "Test Task", completed = false (e.g., using jsonPath).

Include commands to run only this test using Maven or Gradle.

**Acceptance Criteria**:

The test class is annotated with @SpringBootTest and @AutoConfigureMockMvc.

TaskService is mocked with @MockBean.

The test performs a request using MockMvc and includes Basic auth.

The test verifies HTTP 200 OK.

The test verifies the JSON response body fields.

The test compiles and passes.

**Deliverables**:

Full code for TaskControllerTest.java.

Commands to run the test.

**Run Only This Test**:

Maven: mvn test -Dtest=com.example.demo.controller.TaskControllerTest

Gradle (Windows): gradlew.bat test --tests "com.example.demo.controller.TaskControllerTest"

Gradle (Unix/macOS): ./gradlew test --tests "com.example.demo.controller.TaskControllerTest"
