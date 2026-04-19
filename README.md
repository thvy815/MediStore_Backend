# MediStore Backend - Pharmacy E-Commerce & Management System

## 1. Introduction

This project is the backend system of a pharmacy e-commerce and management platform, developed using Java 17 and Spring Boot.  

The system provides RESTful APIs to support both customer-facing features and internal management operations, including product management, order processing, authentication, and inventory control.

The application is designed with a layered architecture to ensure scalability, maintainability, and clear separation of concerns.

---

## 2. Features

- Authentication and Authorization
  - JWT-based authentication
  - Role-based access control (Admin, Customer, Warehouse Staff, Customer Service Staff, Pharmacist, Accountant)

- Product and Inventory Management
  - CRUD operations for products
  - Inventory tracking and stock management
  - Automatic stock updates after order placement

- Order Management
  - Multi-step checkout workflow (cart, shipping, payment, review)
  - Order creation and tracking
  - Order history management

- User Management
  - User registration and login
  - Profile management

- System Design
  - RESTful API architecture
  - Layered structure: Controller – Service – Repository

---

## 3. Technology Stack

- Backend: Java 17, Spring Boot 3.x
- Frameworks: Spring Web, Spring Data JPA, Spring Security
- Database: PostgreSQL (Neon)
- Build Tool: Maven
- Tools: Docker, Postman, Swagger
- Deployment: Render

---

## 4. Project Status

This project is currently under active development and continuous improvement.

Core functionalities such as authentication, product management, order processing, and inventory synchronization have been implemented. Additional features and optimizations are being developed to improve performance, scalability, and user experience.

---

## 5. Planned Features

- Payment integration with external gateways
- Voucher and discount system enhancement
- Advanced reporting and analytics
- Email notifications (order confirmation, password reset)
- Real-time updates using WebSocket
- Unit and integration testing
- Performance optimization and caching mechanisms

---

## 6. System Architecture

The application follows a layered architecture:

- Controller: Handles HTTP requests and responses
- Service: Contains business logic
- Repository: Handles data access using JPA

This structure improves maintainability, scalability, and code organization.

---

## 7. API Documentation (Swagger)

The project integrates Swagger UI for API documentation and testing.

You can access the interactive API documentation at:

Local:
http://localhost:8080/swagger-ui/index.html

Production:
https://medistore-backend-i0de.onrender.com/swagger-ui/index.html

Swagger allows developers to:
- Explore and understand API endpoints
- Execute requests directly
- Inspect request and response structures

---

## 8. Installation and Setup (Local)

### 8.1 Requirements

- JDK 17
- Maven
- PostgreSQL database

### 8.2 Clone the repository

```bash
git clone https://github.com/thvy815/MediStore_Backend.git
cd MediStore_Backend
```

### 8.3 Configure environment variables

Create a `.env` file in the root directory with the following variables:

```env
DB_URL=your_database_url
DB_USER=your_username
DB_PASSWORD=your_password
APP_JWT_SECRET=your_secret_key
```

### 8.4 Run the application

Using Maven:

```bash
./mvnw spring-boot:run
```

### 8.5 Application runs at

http://localhost:8080

## 9. Deployment

The backend is deployed on Render and connected to a cloud PostgreSQL database (Neon).

## 10. Notes

Some features are currently under development or planned for future implementation. The project is continuously being improved as part of learning and real-world system development practice.

This project was developed as part of a team-based academic project in a Software Engineering course.

It demonstrates practical experience in designing and implementing a real-world system, including backend development, API design, and system architecture. The project emphasizes collaboration, problem-solving, and applying theoretical knowledge to real-world scenarios.
