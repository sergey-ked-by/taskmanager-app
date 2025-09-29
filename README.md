# Task Manager Application

A robust and feature-rich task management application built with Java and the Spring Boot framework. This project serves as a comprehensive example of a modern web application, incorporating a clean architecture, security best practices, and a full suite of unit tests.

## Features

*   **User Authentication:** Secure user registration and login functionality using Spring Security and JWT (JSON Web Tokens).
*   **Task Management:** Create, Read, Update, and Delete (CRUD) operations for tasks.
*   **Role-Based Access Control:** Clear distinction between regular users (can manage their own tasks) and administrators (can manage all users and tasks).
*   **RESTful API:** A complete REST API for programmatic access to the application's features.
*   **API Documentation:** Integrated Swagger UI for easy exploration and testing of the API endpoints.
*   **Web Interface:** A server-side rendered web interface built with Thymeleaf for user-friendly interaction.
*   **Database Migrations:** Uses Flyway for version-controlled database schema management.

## Tech Stack

*   **Backend:**
    *   Java 17
    *   Spring Boot 3
    *   Spring Security (with JWT)
    *   Spring Data JPA
    *   Spring Web
    *   Spring Cache
*   **Frontend:**
    *   Thymeleaf
    *   HTML/CSS
*   **Database:**
    *   PostgreSQL (for production)
    *   H2 (for development and testing)
    *   Flyway (for migrations)
*   **Build & Testing:**
    *   Gradle
    *   JUnit 5
    *   Mockito
*   **API Documentation:**
    *   SpringDoc (Swagger UI)

## Getting Started

### Prerequisites

*   JDK 17 or later
*   Gradle 8.5 or later (optional, the Gradle Wrapper is included)
*   A running PostgreSQL instance (optional, the application can run with the in-memory H2 database by default).

### Build

To build the project and run the unit tests, execute the following command in the root directory:

```bash
./gradlew check
```

### Run the Application

You can run the application using the Spring Boot Gradle plugin:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

By default, the application uses an in-memory H2 database. You can configure it to use a PostgreSQL database by modifying the `src/main/resources/application.yml` file.

### Accessing the Application

*   **Web Interface:** Open your browser and navigate to `http://localhost:8080`.
*   **API Documentation (Swagger UI):** `http://localhost:8080/swagger-ui.html`

## Project Structure

*   `src/main/java`: Main application source code.
    *   `config`: Spring configuration files (Security, OpenAPI).
    *   `controller`: Spring MVC controllers for both the web UI and the REST API.
    *   `dto`: Data Transfer Objects used for API requests and responses.
    *   `exception`: Custom exception classes and a global exception handler.
    *   `mapper`: MapStruct mappers for converting between DTOs and entities.
    *   `model`: JPA entity classes.
    *   `repository`: Spring Data JPA repositories.
    *   `security`: JWT utility and filter classes.
    *   `service`: Business logic layer.
*   `src/main/resources`: Application resources.
    *   `application.yml`: Main application configuration.
    *   `db/migration`: Flyway SQL migration scripts.
    *   `static`: Static assets (CSS, JS, images).
    *   `templates`: Thymeleaf templates for the web UI.
*   `src/test/java`: Unit tests.
