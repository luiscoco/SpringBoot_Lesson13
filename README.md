# SpringBoot_Lesson13

**IMPORTANT NOTE**:

Copy and paste the Lesson 10 source code and open it with VSCode. Then select Codex agent in VSCode and execute the following prompt:

## Propmt for the Code Agent (Codex, Gemini Code Assistant or Copilot)

**Context**

Set up a complete local observability stack for a Spring Boot 3.3 application named task-service using Docker Compose.

Target OS may be Windows; prefer healthchecks that avoid fragile quoting.

**Task**

**Generate**:

docker-compose.yml with pinned images, healthchecks, and Grafana provisioning mounts

prometheus.yml scraping task-service

Grafana provisioning files (datasource + dashboard provider) and a minimal Spring Boot dashboard JSON

A Dockerfile for a Spring Boot fat JAR (includes wget for healthchecks)

An application.properties snippet enabling actuator metrics and OTLP tracing to Jaeger

Ensure Spring app dependencies include Actuator, Prometheus registry, and Micrometer OTLP tracing; and allow unauthenticated access to /actuator/** if Spring Security is enabled

**Constraints**

Service name must be task-service everywhere (Compose, Prometheus target, app name).

Build task-service from a local Dockerfile in . and map 8080 (line 8080).

All services share a single network observability.

**Pin images:**

Prometheus prom/prometheus:v2.53.0

Grafana grafana/grafana:10.4.6

Jaeger all-in-one jaegertracing/all-in-one:1.74.0

**Expose ports:**

App 8080, Prometheus 9090, Grafana 3000

Jaeger UI 16686, OTLP gRPC 4317, OTLP HTTP 4318

**Healthchecks and restart policy:**

restart: unless-stopped for all services

task-service healthcheck: GET http://localhost:8080/actuator/health and check body contains UP (use wget). Use start_period: 60s and retries: 12.

prometheus healthcheck: http://localhost:9090/-/ready

grafana healthcheck: http://localhost:3000/login

jaeger healthcheck: http://localhost:16686

Use depends_on with condition: service_healthy (Prometheus waits for task-service; Grafana waits for Prometheus).

**Grafana provisioning:**

Mount ./grafana/provisioning/datasources -> /etc/grafana/provisioning/datasources

Mount ./grafana/provisioning/dashboards -> /etc/grafana/provisioning/dashboards

Mount ./grafana/dashboards -> /var/lib/grafana/dashboards

**Prometheus config:**

global.scrape_interval: 15s

Job spring-boot-app, metrics_path: /actuator/prometheus

Static target task-service (line 8080)

**Compose specifics:**

Do not include the version key (Compose v2 ignores it).

Mount ./prometheus.yml -> /etc/prometheus/prometheus.yml:ro

**Dockerfile:**

Base eclipse-temurin:21-jre

Install wget (for healthcheck)

ARG JAR_FILE=target/*.jar, COPY to /app/app.jar

EXPOSE 8080 and ENTRYPOINT ["java","-jar","/app/app.jar"]

**App config assumptions:**

Actuator and Prometheus metrics enabled and exposed

Micrometer Tracing (OTLP) configured to Jaeger OTLP HTTP at http://jaeger:4318/v1/traces

Sampling probability set to 1.0

If Spring Security is present, permit /actuator/** without authentication

**Steps**

**Create prometheus.yml:**

scrape_interval: 15s

Job spring-boot-app

metrics_path: /actuator/prometheus

Targets ["task-service:8080"]

**Create docker-compose.yml:**

Services: task-service, prometheus, grafana, jaeger

Pin images as specified

restart: unless-stopped

Healthchecks: task-service uses wget + grep -q UP, start_period 60s, retries 12; Prometheus/Grafana/Jaeger as above

depends_on with health conditions

Mount prometheus.yml and Grafana provisioning/dashboards

Expose ports as specified and place all services on observability network

**Create Grafana provisioning:**

grafana/provisioning/datasources/datasource.yml pointing to http://prometheus:9090

grafana/provisioning/dashboards/dashboard.yml loading from /var/lib/grafana/dashboards

grafana/dashboards/spring-boot.json with panels: uptime, RPS by uri/status, avg latency, p95 latency, JVM memory, heap usage %

Create Dockerfile for Spring Boot fat JAR as specified (including wget).

Provide application.properties snippet enabling actuator and tracing.

Ensure the app has the required dependencies; if missing, add to pom.xml:

org.springframework.boot:spring-boot-starter-actuator

io.micrometer:micrometer-registry-prometheus

io.micrometer:micrometer-tracing-bridge-otel

io.opentelemetry:opentelemetry-exporter-otlp

If Spring Security is configured, update security to permit /actuator/** anonymously.

**Deliverables**

docker-compose.yml

prometheus.yml

grafana/provisioning/datasources/datasource.yml

grafana/provisioning/dashboards/dashboard.yml

grafana/dashboards/spring-boot.json

Dockerfile

application.properties snippet:

spring.application.name=task-service

management.endpoints.web.exposure.include=health,info,prometheus

management.endpoint.prometheus.enabled=true

management.metrics.export.prometheus.enabled=true

management.tracing.enabled=true

management.tracing.sampling.probability=1.0

management.otlp.tracing.endpoint=http://jaeger:4318/v1/traces

**Run Notes**

Build the JAR first: mvn -q -DskipTests package

Start: docker compose up --build

Prometheus quick queries: up, process_uptime_seconds{job="spring-boot-app"}, http_server_requests_seconds_count{job="spring-boot-app"}






