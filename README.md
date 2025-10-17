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

---

## Azure Deployment (Internship Project)

This project is configured for a full CI/CD deployment to **Microsoft Azure** using Infrastructure as Code (IaC) and GitHub Actions.

> **Note:** The infrastructure for this project is running on a limited-time Azure Free Trial account. It is expected to be decommissioned and will go offline after **November 15, 2025**.

### Technology Stack

- **Cloud Provider**: Microsoft Azure
- **IaC Tooling**: Terraform with Terragrunt wrapper
- **Container Registry**: Azure Container Registry (ACR)
- **Database**: Azure Database for PostgreSQL (Flexible Server)
- **Orchestration**: Azure Kubernetes Service (AKS)
- **CI/CD**: GitHub Actions

### Infrastructure

The entire cloud infrastructure is defined in the `/terragrunt` directory and is broken down into modular components:

- `vnet`: Creates the virtual network, subnets, and private DNS zone.
- `db`: Creates the PostgreSQL server and database within the VNet.
- `acr`: Creates the Azure Container Registry for storing Docker images.
- `aks`: Creates the Kubernetes cluster, configured to run in the VNet and pull images from ACR.

Each component can be deployed manually by navigating to its directory (e.g., `terragrunt/envs/azure-testing/vnet`) and running `terragrunt apply`.

### CI/CD Automation

The repository contains a fully automated CI/CD pipeline defined in `.github/workflows/deploy-to-aks.yml`.

**Trigger**: The workflow runs automatically on every `push` to the `main` branch.

**Process**:
1.  **Login**: The workflow authenticates with Azure using a Service Principal.
2.  **Build & Push**: It builds the Java application's Docker image and pushes it to the project's private Azure Container Registry (ACR), tagging it with the commit SHA.
3.  **Prepare Manifests**: It dynamically updates the Kubernetes manifests in the `/k8s` directory, injecting the new image tag and database credentials (from GitHub Secrets).
4.  **Deploy**: It connects to the AKS cluster and applies the updated manifests, triggering a rolling update of the application.

### Accessing the Deployed Application

After the `deploy-to-aks.yml` workflow completes successfully, it may take a few minutes for the Azure Load Balancer to provision a public IP address.

To find the public IP, you can run the following `kubectl` command (after configuring access to the cluster):

```bash
kubectl get service taskmanager-app-service --watch
```

Once the `EXTERNAL-IP` changes from `<pending>` to an IP address, you can access the application by navigating to that IP in your web browser.
