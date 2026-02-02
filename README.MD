# Spring Boot + ScyllaDB + Testcontainers — Example Project

This repository demonstrates how to use Testcontainers with ScyllaDB in a Spring Boot application to run integration tests against a disposable ScyllaDB instance.

File: `README.md`

## Overview

This sample shows:
- How to start a ScyllaDB container for tests using Testcontainers.
- How to initialize keyspace and table schema inside the container.
- How to wire the container's connection properties into Spring Boot tests.
- A simple integration test that inserts a row using the DataStax Java driver.

## Prerequisites

- Java 17+ (or the JDK configured for this project)
- Maven
- Docker (running and accessible on your machine)
- Recommended: enable Testcontainers reuse if you want faster consecutive runs

## Project Structure (important files)

- [SpringBootScylladbTestcontainersDemoApplicationTests](./src/test/java/com/example/springbootscylladbtestcontainersdemo/SpringBootScylladbTestcontainersDemoApplicationTests.java) —
  Spring Boot application entry.
- [TestcontainersConfiguration](./src/test/java/com/example/springbootscylladbtestcontainersdemo/TestcontainersConfiguration.java)—
  Testcontainers configuration and schema initialization.
- [TestSpringBootScylladbTestcontainersDemoApplication](./src/test/java/com/example/springbootscylladbtestcontainersdemo/TestSpringBootScylladbTestcontainersDemoApplication.java) —
  Test application runner that applies the initializer.
- [SpringBootScylladbTestcontainersDemoApplicationTests](./src/test/java/com/example/springbootscylladbtestcontainersdemo/SpringBootScylladbTestcontainersDemoApplicationTests.java) —
  Example integration test using `CqlSession`.

## How it works — key points

1. Test startup
    - Tests use `@ContextConfiguration(initializers = TestcontainersConfiguration.class)` so Spring test context is initialized with the Scylla container configuration before beans are created.

2. `TestcontainersConfiguration`
    - Creates a `ScyllaDBContainer` (image `scylladb/scylla:6.2`).
    - Uses `.withCreateContainerCmdModifier(...)` to bind container port `19042` to host port `29042` (so host access is stable).
    - Calls `scyllaDB.execInContainer("cqlsh", "-e", "...")` to create the `demo_key_space` keyspace and `users` table before tests run.
    - Exposes connection properties to Spring environment via `TestPropertyValues.of(...)`:
        - `spring.cassandra.contact-points`
        - `spring.cassandra.port`
        - `spring.cassandra.local-datacenter`
        - `spring.cassandra.keyspace-name`

3. Tests
    - The integration test autowires a `CqlSession`.
    - It verifies keyspace and table exist, then performs an insert using the DataStax driver / query builder.

## Run locally

1. Start Docker.
2. Build and run tests:
