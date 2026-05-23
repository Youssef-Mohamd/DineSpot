<<<<<<< HEAD
**<div align="center">

# 🍽️ Restaurant Reservation System

### A full-stack web application for online restaurant table booking

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

</div>

---

## 📌 Overview

**Restaurant Reservation System** is a full-stack web application that allows customers to discover restaurants, check real-time table availability, and book reservations online.

Restaurant admins manage their tables, time slots, and bookings through a dedicated dashboard — all backed by a secure REST API built with Spring Boot.

---

## ✨ Features

### 👤 Customer
- Register & Login with JWT authentication
- Browse and search restaurants by city or cuisine
- Check real-time table availability by date and guest count
- Book a table — system auto-assigns the best fitting table
- View, track, and cancel reservations
- Receive in-app notifications for booking confirmations and cancellations

### 🔧 Admin
- Create and manage restaurant profile
- Add and manage tables (capacity, location)
- Define available time slots
- View, confirm, and cancel reservations
- Dashboard with today's bookings overview

---

## 🏗️ Architecture

The backend follows a **5-Layer Architecture**:

```
Controller  →  Service  →  Repository  →  Entity  →  Database
     ↑              ↑
    DTO           Strategy / Observer / Singleton
```

---

## 🗂️ Project Structure

```
reservation/
├── src/main/java/com/restaurant/reservation/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── RestaurantController.java
│   │   ├── TableController.java
│   │   ├── TimeSlotController.java
│   │   ├── ReservationController.java
│   │   ├── AvailabilityController.java
│   │   └── NotificationController.java
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── RestaurantService.java
│   │   ├── TableService.java
│   │   ├── TimeSlotService.java
│   │   ├── ReservationService.java
│   │   ├── AvailabilityService.java
│   │   ├── NotificationService.java
│   │   └── TableAssignmentService.java
│   │
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RestaurantRepository.java
│   │   ├── RestaurantTableRepository.java
│   │   ├── TimeSlotRepository.java
│   │   ├── ReservationRepository.java
│   │   └── NotificationRepository.java
│   │
│   ├── entity/
│   │   ├── User.java
│   │   ├── Restaurant.java
│   │   ├── RestaurantTable.java
│   │   ├── TimeSlot.java
│   │   ├── Reservation.java
│   │   └── Notification.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── CreateRestaurantRequest.java
│   │   │   ├── CreateTableRequest.java
│   │   │   ├── CreateTimeSlotRequest.java
│   │   │   └── CreateReservationRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── UserResponse.java
│   │       ├── RestaurantResponse.java
│   │       ├── TableResponse.java
│   │       ├── TimeSlotResponse.java
│   │       ├── ReservationResponse.java
│   │       └── NotificationResponse.java
│   │
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── JwtFilter.java
│   │   ├── SecurityConfig.java
│   │   └── UserDetailsServiceImpl.java
│   │
│   ├── strategy/
│   │   ├── TableAssignmentStrategy.java
│   │   └── BestFitAssignment.java
│   │
│   └── event/
│       ├── ReservationEvent.java
│       └── ReservationEventListener.java
│
├── src/main/resources/
│   └── application.properties
│
└── pom.xml
```

---

## 🎨 Design Patterns

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Strategy** | `TableAssignmentService` | Selects the best available table based on guest count. Easily swap algorithms without changing core logic. |
| **Observer** | `ReservationEventListener` | Automatically triggers notifications when a reservation is created or cancelled — decouples services. |
| **Singleton** | All `@Service` & `@Repository` | Spring IoC Container creates one shared instance per class across the entire application lifecycle. |

---

## 🗄️ Database Schema

```
users ──────────────── reservations ──── restaurant_tables
  │                         │                    │
  │                         └──── time_slots      │
  │                         └──── restaurants ────┘
  │
  └── notifications
```

**Tables:** `users` · `restaurants` · `restaurant_tables` · `time_slots` · `reservations` · `notifications`

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- MySQL 8.x
- Maven 3.x

```

The API will be available at: `http://localhost:8081/api`

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/me` | Authenticated |

### Restaurants
| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/restaurants` | Public |
| GET | `/api/restaurants/:id` | Public |
| POST | `/api/restaurants` | ADMIN |
| PUT | `/api/restaurants/:id` | ADMIN |

### Reservations
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/reservations` | CUSTOMER |
| GET | `/api/reservations/me` | CUSTOMER |
| PUT | `/api/reservations/:id/confirm` | ADMIN |
| PUT | `/api/reservations/:id/cancel` | Authenticated |

### Availability
| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/availability?restaurantId=&date=&guests=` | Public |

---

## 🔐 Authentication

All protected endpoints require a JWT token in the request header:

```
Authorization: Bearer <your_token>
```

---

## 👥 User Roles

| Role | Description |
|---|---|
| `CUSTOMER` | Browse restaurants, book and manage own reservations |
| `ADMIN` | Manage restaurant, tables, time slots, and all reservations |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 + Spring Boot 4.0.6 |
| Security | Spring Security + JWT |
| Database | MySQL 8.x + Spring Data JPA |
| Frontend | Angular 17 |
| Build Tool | Maven |

---

## 📄 License

This project is built for academic purposes.**
=======
# DineSpot

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.7.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
>>>>>>> 1cfb6d8 (front-end initial commit)
