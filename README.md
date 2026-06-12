# Finance Tracker API

A RESTful API for personal finance tracking built with Spring Boot 4, featuring JWT authentication, PostgreSQL database, and Docker containerization.

## 🚀 Live Demo

API Base URL: `https://finance-tracker-production-3774.up.railway.app`

Swagger UI: `https://finance-tracker-production-3774.up.railway.app/swagger-ui/index.html`

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 4**
- **Spring Security** + **JWT** authentication
- **PostgreSQL** + **Flyway** migration
- **Docker** + **Docker Compose**
- **Railway** cloud deployment
- **Springdoc OpenAPI** (Swagger UI)

## ✨ Features

- User registration and login with JWT authentication
- Full CRUD for financial transactions
- Filter transactions by category, type, and date range
- Monthly summary (total income, expense, balance)
- Pagination support
- Database indexing for query optimization
- Structured logging with correlation ID
- Health check via Spring Actuator

## 📋 Prerequisites

- Java 17
- Docker & Docker Compose
- PostgreSQL (or use Docker)

## 🏃 Running Locally

### Option 1 — With Docker Compose (recommended)

```bash
git clone https://github.com/abdisetiawan/finance-tracker.git
cd finance-tracker
docker-compose up --build
```

App will be available at `http://localhost:8080`

### Option 2 — With IntelliJ

1. Clone the repository
2. Create `src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/financedb
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver

app:
  jwt:
    secret: your_jwt_secret
    expiration: 86400000
```

3. Set active profile to `local`
4. Run `FinanceTrackerApplication`

## 📚 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create transaction |
| GET | `/api/transactions` | Get all transactions |
| GET | `/api/transactions/paginated` | Get transactions with pagination |
| GET | `/api/transactions/filter/category` | Filter by category |
| GET | `/api/transactions/filter/type` | Filter by type |
| GET | `/api/transactions/filter/date` | Filter by date range |
| GET | `/api/transactions/summary` | Get monthly summary |
| PUT | `/api/transactions/{id}` | Update transaction |
| DELETE | `/api/transactions/{id}` | Delete transaction |

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | Get all available categories |

### Health
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Application health check |

## 🔐 Authentication

This API uses JWT Bearer token authentication.

1. Register or login to get a token
2. Include the token in request headers:
   Authorization: Bearer <your_token>

## 🐳 Docker

Build and run with Docker Compose:

```bash
# Start all services
docker-compose up --build

# Run in background
docker-compose up -d

# Stop all services
docker-compose down
```

## 📊 Database Schema
users

├── id, email, password_hash, full_name, created_at
categories

├── id, name, type (INCOME/EXPENSE), user_id (null = default)
transactions

├── id, user_id, category_id, amount, type, note, date, created_at

## 🏗️ Project Structure
src/main/java/com/kamu/finance_tracker/

├── config/          # Security, JWT, CORS, Swagger config

├── controller/      # REST controllers

├── dto/             # Request/Response DTOs

├── entity/          # JPA entities

├── exception/       # Global exception handler

├── repository/      # Spring Data JPA repositories

└── service/         # Business logic