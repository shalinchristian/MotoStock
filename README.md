# MotoStock

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F)
![AngularJS](https://img.shields.io/badge/AngularJS-1.8-DD0031)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

MotoStock is a management app for a motorcycle dealership. It tracks inventory and stock alerts, records sales, and books test rides, all from one dashboard.

![MotoStock dashboard](docs/screenshots/02-dashboard-charts.png)

---

## Features

- **Bike catalog** with photos, specs, and search by brand, category, price, and stock. Electric bikes show motor power in kW.
- **Stock tracking** with a low-stock alert threshold for each bike
- **Sales** that reduce stock automatically and refuse to oversell
- **Test-ride bookings** on a monthly calendar that blocks clashing time slots
- **Dashboard** with stock and sales charts
- **JWT login** with two roles. Staff manage stock, sales, and bookings, and only admins can delete bikes.
- **Swagger docs** for every endpoint

---

## Screenshots

| | |
|---|---|
| ![Recent sales and low-stock alerts](docs/screenshots/03-dashboard-sales-and-alerts.png)<br>*Recent sales and low-stock alerts* | ![Bike catalog](docs/screenshots/04-catalog.png)<br>*Bike catalog* |
| ![Filtered catalog](docs/screenshots/05-catalog-filtered-sport.png)<br>*Filtered catalog* | ![Edit a bike](docs/screenshots/06-edit-bike-form.png)<br>*Edit a bike* |
| ![Electric bike form with kW](docs/screenshots/07-add-electric-bike-form.png)<br>*Electric bike form with kW* | ![Sales](docs/screenshots/08-sales.png)<br>*Sales* |
| ![Test-ride calendar](docs/screenshots/09-test-ride-calendar.png)<br>*Test-ride calendar* | ![Staff login](docs/screenshots/01-login.png)<br>*Staff login* |
| ![Swagger API docs](docs/screenshots/10-swagger-ui.png)<br>*Swagger API docs* |  |

---

## Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 3.5, Spring Data JPA, Spring Security with JWT, Bean Validation, springdoc OpenAPI |
| Database | H2 for development, MySQL for production |
| Frontend | AngularJS 1.8, Chart.js, Express (static files and API proxy) |
| Tests | JUnit 5, Mockito, MockMvc |
| Deployment | Docker Compose (MySQL, API, and web) |

---

## Getting Started

**With Docker**

```bash
cp .env.example .env
docker compose up --build
```

- Web app: http://localhost:4000
- API: http://localhost:8080 (Swagger at `/swagger-ui.html`)

**Without Docker**

```bash
cd backend && ./mvnw spring-boot:run      # H2 in memory, seeded with demo data
cd web && npm install && npm start        # in a second terminal, http://localhost:4000
```

**Demo logins:** `admin` / `admin123` and `staff` / `staff123`. Change them with `ADMIN_PASSWORD` and `STAFF_PASSWORD`.

**Tests:** run `cd backend && ./mvnw test`.

---

## API

| Method | Path | Access |
|---|---|---|
| GET | `/api/motorcycles` (filters: `q`, `brand`, `category`, `minPrice`, `maxPrice`, `inStock`, `sort`) | public |
| GET | `/api/motorcycles/{id}`, `/low-stock`, `/brands` | public |
| POST, PUT | `/api/motorcycles`, `/api/motorcycles/{id}` | staff, admin |
| PATCH | `/api/motorcycles/{id}/stock` | staff, admin |
| DELETE | `/api/motorcycles/{id}` | admin |
| GET, POST | `/api/sales` | staff, admin |
| GET, POST, PATCH, DELETE | `/api/test-rides` | staff, admin |
| GET | `/api/dashboard` | staff, admin |
| POST, GET | `/api/auth/login`, `/api/auth/me` | public, token |

---

## Project Structure

```
backend/   Spring Boot API: controller, dto, mapper, service (interfaces + impl), entity,
           repository, security, config, exception
web/       AngularJS app (MotoApp/) served by an Express server that proxies /api
docker-compose.yml
```

See `DEALERSHIP.md` for detailed notes.

## Connect With Me

**Shalin Christian**, [GitHub](https://github.com/shalinchristian)
