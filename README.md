# Motorcycle Inventory Tracker API

A RESTful API built with **Spring Boot** for managing a motorcycle inventory. Supports full CRUD operations with persistent storage via **PostgreSQL** and data access handled through **Spring Data JPA**.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Reference](#api-reference)
  - [Base URL](#base-url)
  - [Endpoints](#endpoints)
  - [Request & Response Examples](#request--response-examples)
- [Data Model](#data-model)
- [Validation Rules](#validation-rules)
- [Architecture](#architecture)
- [Connect With Me](#connect-with-me)

---

## Overview

The Motorcycle Inventory Tracker API allows users to manage a collection of motorcycle records. Each motorcycle entry contains details such as brand, model, year, price, and availability. The API is designed following RESTful conventions and uses a layered architecture (Controller → Service → Repository → Entity) for clean separation of concerns.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5 |
| Language | Java 17 |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Build Tool | Maven |
| Boilerplate Reduction | Lombok |
| Validation | Spring Boot Starter Validation |

---

## Project Structure

```
motorcycle-inventory-tracker/
├── src/
│   └── main/
│       ├── java/com/shalin/motorcycle_inventory_tracker/
│       │   ├── controller/
│       │   │   └── MotorcycleController.java      # REST endpoints
│       │   ├── service/
│       │   │   └── MotorcycleService.java          # Business logic
│       │   ├── repository/
│       │   │   └── MotorcycleRepository.java       # JPA repository
│       │   ├── model/
│       │   │   └── Motorcycle.java                 # JPA entity
│       │   └── MotorcycleInventoryTrackerApplication.java
│       └── resources/
│           └── application.properties              # DB config
├── pom.xml
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Any REST client (Postman, Insomnia, curl)

### Database Setup

Create a PostgreSQL database for this project:

```sql
CREATE DATABASE motorcycle_inventory;
```

### Configuration

Update `src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/motorcycle_inventory
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

> **Note:** `ddl-auto=update` will automatically create or update the `motorcycles` table on startup. No manual SQL schema setup is required.

### Running the Application

**Using Maven Wrapper (recommended):**

```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

**Or build and run the JAR:**

```bash
./mvnw clean package
java -jar target/motorcycle-inventory-tracker-0.0.1-SNAPSHOT.jar
```

The server starts at `http://localhost:8080` by default.

---

## API Reference

### Base URL

```
http://localhost:8080/api/motorcycles
```

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/motorcycles` | Retrieve all motorcycles |
| `GET` | `/api/motorcycles/{id}` | Retrieve a motorcycle by ID |
| `POST` | `/api/motorcycles` | Add a new motorcycle |
| `PUT` | `/api/motorcycles/{id}` | Update an existing motorcycle |
| `DELETE` | `/api/motorcycles/{id}` | Delete a motorcycle by ID |

---

### Request & Response Examples

#### GET all motorcycles

**Request:**
```http
GET /api/motorcycles
```

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "brand": "Honda",
    "model": "CBR600RR",
    "year": 2022,
    "price": 11999.99,
    "available": true
  },
  {
    "id": 2,
    "brand": "Yamaha",
    "model": "MT-07",
    "year": 2023,
    "price": 7699.00,
    "available": false
  }
]
```

---

#### GET motorcycle by ID

**Request:**
```http
GET /api/motorcycles/1
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "brand": "Honda",
  "model": "CBR600RR",
  "year": 2022,
  "price": 11999.99,
  "available": true
}
```

**Response `404 Not Found`:**
```json
{
  "message": "Motorcycle not found with id: 1"
}
```

---

#### POST — create a motorcycle

**Request:**
```http
POST /api/motorcycles
Content-Type: application/json
```

```json
{
  "brand": "Kawasaki",
  "model": "Ninja 400",
  "year": 2023,
  "price": 5299.00,
  "available": true
}
```

**Response `201 Created`:**
```json
{
  "id": 3,
  "brand": "Kawasaki",
  "model": "Ninja 400",
  "year": 2023,
  "price": 5299.00,
  "available": true
}
```

---

#### PUT — update a motorcycle

**Request:**
```http
PUT /api/motorcycles/3
Content-Type: application/json
```

```json
{
  "brand": "Kawasaki",
  "model": "Ninja 400",
  "year": 2023,
  "price": 4899.00,
  "available": false
}
```

**Response `200 OK`:**
```json
{
  "id": 3,
  "brand": "Kawasaki",
  "model": "Ninja 400",
  "year": 2023,
  "price": 4899.00,
  "available": false
}
```

---

#### DELETE — remove a motorcycle

**Request:**
```http
DELETE /api/motorcycles/3
```

**Response `204 No Content`**

---

## Data Model

### Motorcycle Entity

| Field | Type | Constraints | Description |
|---|---|---|---|
| `id` | `Long` | Auto-generated (PK) | Unique identifier |
| `brand` | `String` | Not blank | Manufacturer name |
| `model` | `String` | Not blank | Model name |
| `year` | `Integer` | Not null | Manufacturing year |
| `price` | `Double` | Not null | Price in USD |
| `available` | `Boolean` | Not null | Availability status |

---

## Validation Rules

Input validation is enforced via `spring-boot-starter-validation`. Requests with missing or blank required fields will return a `400 Bad Request` with a descriptive error message.

- `brand` — must not be blank (`@NotBlank`)
- `model` — must not be blank (`@NotBlank`)
- `year` — must not be null (`@NotNull`)
- `price` — must not be null (`@NotNull`)
- `available` — must not be null (`@NotNull`)

---

## Architecture

This project follows a standard **layered Spring Boot architecture**:

```
Client (HTTP Request)
        │
        ▼
 ┌─────────────────┐
 │   Controller    │  @RestController — handles HTTP, delegates to service
 └────────┬────────┘
          │
          ▼
 ┌─────────────────┐
 │    Service      │  @Service — business logic, uses Optional for null-safe returns
 └────────┬────────┘
          │
          ▼
 ┌─────────────────┐
 │   Repository    │  @Repository — extends JpaRepository, no SQL needed
 └────────┬────────┘
          │
          ▼
 ┌─────────────────┐
 │     Entity      │  @Entity — maps to `motorcycles` table via Hibernate
 └─────────────────┘
          │
          ▼
     PostgreSQL
```

Key Spring/JPA annotations used across the project:

- **Controller:** `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PathVariable`, `@RequestBody`
- **Service:** `@Service`
- **Repository:** `@Repository` — extends `JpaRepository` for `findAll()`, `findById()`, `save()`, `deleteById()`
- **Entity:** `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- **Validation:** `@NotBlank`, `@NotNull`
- **Lombok:** `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- **Utilities:** `ResponseEntity` (HTTP response wrapper), `Optional` with `orElseThrow()`

---

## Connect With Me

- **GitHub:** [github.com/shalinchristian](https://github.com/shalinchristian)
- **LinkedIn:** [linkedin.com/in/shalinvchristian](https://linkedin.com/in/shalinvchristian)
