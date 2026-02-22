# Student Management System

A Spring Boot web application for managing students, courses, and departments with role-based access control.

## Features

- **3 Roles**: Student, Teacher, Authority with different permissions
- **Entity Relationships**: 
  - Department:Student (1:M)
  - Course:Student (M:M)
- **CRUD Operations**: Full Create, Read, Update, Delete functionality
- **Role-Based Access Control**:
  - Students can only view data
  - Teachers can manage students and departments
  - Authority can manage everything including courses
- **RESTful API**: Versioned API at `/api/v1/`
- **Spring Security**: Authentication and authorization
- **Docker Support**: Multi-stage Dockerfile and docker-compose

## Tech Stack

- Java 17
- Spring Boot 3.4.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Docker

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for containerized deployment)
- PostgreSQL (for local development)

### Local Development

1. **Start PostgreSQL** (or use Docker):
   ```bash
   docker run -d --name postgres -e POSTGRES_DB=admindb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -p 5432:5432 postgres:16
   ```

2. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**: http://localhost:8080

### Running with Docker

```bash
# Build and run with docker-compose
docker-compose up --build

# Or use the newer syntax
docker compose up --build
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with test profile (uses H2 in-memory database)
./mvnw test -Dspring.profiles.active=test

# Run integration tests
./mvnw verify
```

## API Endpoints

### Students API
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/v1/students` | Get all students | All authenticated |
| GET | `/api/v1/students/{id}` | Get student by ID | All authenticated |
| POST | `/api/v1/students` | Create student | Teacher, Authority |
| PUT | `/api/v1/students/{id}` | Update student | Teacher, Authority |
| DELETE | `/api/v1/students/{id}` | Delete student | Teacher, Authority |

### Courses API
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/v1/courses` | Get all courses | All authenticated |
| GET | `/api/v1/courses/{id}` | Get course by ID | All authenticated |
| POST | `/api/v1/courses` | Create course | **Authority only** |
| PUT | `/api/v1/courses/{id}` | Update course | Teacher, Authority |
| DELETE | `/api/v1/courses/{id}` | Delete course | **Authority only** |

### Departments API
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/v1/departments` | Get all departments | All authenticated |
| GET | `/api/v1/departments/{id}` | Get department by ID | All authenticated |
| POST | `/api/v1/departments` | Create department | Teacher, Authority |
| PUT | `/api/v1/departments/{id}` | Update department | Teacher, Authority |
| DELETE | `/api/v1/departments/{id}` | Delete department | Teacher, Authority |

### API Authentication

REST API uses HTTP Basic Authentication:
```bash
curl -u username:password http://localhost:8080/api/v1/students
```

## CI/CD Pipeline

GitHub Actions workflow (`.github/workflows/ci.yml`) includes:

1. **Test Job**: Runs unit and integration tests with PostgreSQL service
2. **Build Job**: Builds the application JAR
3. **Docker Job**: Builds Docker image

## Branch Protection Rules Setup

To enforce code quality and prevent direct pushes to protected branches, configure the following in GitHub:

### Steps to Configure Branch Protection

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Branches**
3. Click **Add branch protection rule**
4. Enter branch name pattern: `main`

### Recommended Settings

- [x] **Require a pull request before merging**
  - [x] Require approvals (1 or more)
  - [x] Dismiss stale pull request approvals when new commits are pushed
  
- [x] **Require status checks to pass before merging**
  - [x] Require branches to be up to date before merging
  - Select status checks: `test`, `build`
  
- [x] **Require conversation resolution before merging**

- [x] **Do not allow bypassing the above settings**

- [ ] **Allow force pushes** (Keep unchecked)

- [ ] **Allow deletions** (Keep unchecked)

### Example Branch Protection Configuration

```
Branch name pattern: main

✅ Require a pull request before merging
   ├── Required approvals: 1
   └── Dismiss stale approvals: Yes

✅ Require status checks to pass
   ├── Status checks:
   │   ├── test
   │   └── build
   └── Require up-to-date: Yes

✅ Require conversation resolution

❌ Allow force pushes

❌ Allow deletions
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/webapp/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # MVC Controllers
│   │   │   └── api/         # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA Entities
│   │   ├── repository/      # Spring Data Repositories
│   │   └── service/         # Business Logic
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       └── application.yml  # Configuration
└── test/
    └── java/com/example/webapp/
        ├── controller/api/  # REST Controller tests
        ├── integration/     # Integration tests
        └── service/         # Service unit tests
```

## Security Scenarios

| Scenario | Allowed Roles |
|----------|---------------|
| Students cannot delete their account | Only Teacher, Authority can delete |
| Teachers cannot add courses | Only Authority can add courses |
| View all data | All authenticated users |

## License

This project is for educational purposes. 
