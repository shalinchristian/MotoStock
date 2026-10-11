# MotoStock – dealership edition (layout & run notes)

```
backend/                Spring Boot 3.5 (Java 17), package com.shalin.motorcycle_inventory_tracker
  controller/           MotorcycleController (original /api/motorcycles CRUD + search/low-stock/brands/stock),
                        SaleController, TestRideController, DashboardController, AuthController
  dto/                  request/response records (validation on requests)
  mapper/               MotorcycleMapper, SalesMapper (entity <-> DTO)
  service/              interfaces; service/impl/ implementations
  entity/               Motorcycle (+specs, photoUrl, lowStockThreshold, category), Sale, TestRide, AppUser, enums
  repository/           Spring Data JPA
  security/             JwtService, JwtAuthenticationFilter
  config/               SecurityConfig, AppConfig (OpenAPI, Clock), DataSeeder (demo data)
  exception/            GlobalExceptionHandler (+ BusinessException -> 409, not-found -> 404)
  resources/            application.properties, application-dev.properties (H2), application-prod.properties (MySQL)
  Dockerfile
web/                    AngularJS 1.8.2 front end, same style as the FSWD exam-prep apps
  server.js             Express: serves MotoApp/ and proxies /api -> API_URL
  MotoApp/view.html     single page; sections switched with ng-show (Dashboard, Catalog, Sales, Test Rides, Login)
  MotoApp/motoModule.js angular.module('motoApp') + JWT $http interceptor + helper factory + rupees filter
  MotoApp/controllers/  mainCtrl, dashboardCtrl (Chart.js), catalogCtrl, salesCtrl, bookingCtrl (month calendar)
  MotoApp/style.css, MotoApp/img/*.svg (category placeholder photos)
  Dockerfile
docker-compose.yml      mysql + api + web
```

## Run
- **Everything:** `cp .env.example .env && docker compose up --build`, then open http://localhost:4000 (API on :8080, Swagger at http://localhost:8080/swagger-ui.html)
- **Dev without Docker:** `cd backend && ./mvnw spring-boot:run` (dev profile, H2 in memory, seeded), then `cd web && npm install && npm start`
- **Logins:** admin / admin123 (ADMIN), staff / staff123 (STAFF). Override them with ADMIN_PASSWORD / STAFF_PASSWORD.

## API summary
| Method | Path | Auth |
|---|---|---|
| GET | /api/motorcycles?q=&brand=&category=&minPrice=&maxPrice=&inStock=&sort= | public |
| GET | /api/motorcycles/{id}, /low-stock, /brands | public |

Electric bikes (`category: ELECTRIC`) leave `engineCapacityCc` null and require `motorPowerKw`. Every other category requires `engineCapacityCc`. The UI shows "X kW" or "X cc" to match.
| POST, PUT | /api/motorcycles, /api/motorcycles/{id} | staff/admin |
| PATCH | /api/motorcycles/{id}/stock  {delta} | staff/admin |
| DELETE | /api/motorcycles/{id} | admin |
| GET, POST | /api/sales (a sale decrements stock and returns 409 if stock is short) | staff/admin |
| GET, POST | /api/test-rides?from=&to= (409 on a slot clash) | staff/admin |
| PATCH | /api/test-rides/{id}/status, DELETE /api/test-rides/{id} (cancel) | staff/admin |
| GET | /api/dashboard | staff/admin |
| POST | /api/auth/login, GET /api/auth/me | public / token |

The original seven Motorcycle fields, their validation messages, and the `{field: message}` error shape are unchanged.
Breaking change: POST, PUT and DELETE on /api/motorcycles now need a JWT.
The default database moved from PostgreSQL to H2 for dev and MySQL for prod.
