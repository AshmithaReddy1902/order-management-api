📦 Order Management API (Spring Boot)
📌 Overview

The Order Management API is a monolithic backend application built using Spring Boot that supports end-to-end order processing for an e-commerce–style system.
It manages products, inventory, and orders, ensuring transactional consistency between order placement and inventory reservation.

This project demonstrates real-world backend concepts such as layered architecture, database transactions, global exception handling, and RESTful API design.

🏗 Architecture

This application follows a clean layered monolithic architecture:

Controller → Service → Repository → Database

Package Structure
com.ashmitha.ordermanagement
│
├── controller        # REST controllers
├── service           # Business logic & transactions
├── repository        # JPA repositories
├── entity            # JPA entities (DB models)
├── dto               # Request / response DTOs
├── exception         # Global exception handling
└── OrderManagementApiApplication.java

🛠 Tech Stack

Java 25

Spring Boot

Spring Web (REST APIs)

Spring Data JPA

Hibernate

SQL Server (SSMS)

Maven

Postman (API testing)

✨ Features

Create and manage products

Track inventory per product

Place orders with inventory reservation

Cancel orders and release reserved inventory

Transactional integrity using @Transactional

Global exception handling with meaningful API responses

RESTful API design with proper HTTP status codes

📚 Database Design

Main tables:

products

inventory

orders

order_items

Key behaviors:

Inventory is reserved when an order is placed

Inventory is released when an order is cancelled

Orders and inventory updates occur within a single transaction

🔗 API Endpoints
➤ Product APIs
Method	Endpoint	Description
POST	/api/products	Create a product
GET	/api/products/{id}	Get product by ID
➤ Order APIs
Method	Endpoint	Description
POST	/api/orders	Place a new order
GET	/api/orders/{id}	Get order by ID
POST	/api/orders/{id}/cancel	Cancel an order
📥 Sample Requests
Create Product
{
  "sku": "SKU-1001",
  "name": "Wireless Mouse",
  "price": 19.99,
  "initialStock": 50
}

Place Order
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}

Cancel Order
POST /api/orders/1/cancel

❗ Error Handling

The application uses global exception handling (@RestControllerAdvice) to return consistent JSON error responses.

Example:

{
  "status": 400,
  "message": "Insufficient inventory for product 1",
  "timestamp": "2026-01-12T20:37:28"
}


This prevents leaking stack traces and improves API usability.

▶️ How to Run Locally
Prerequisites

Java 25 installed

SQL Server running

Maven installed

IDE: Spring Tool Suite (STS) recommended

Steps

Clone the repository

Configure database connection in application.properties

Create database:

CREATE DATABASE OrderManagementDB;


Run the application:

Run As → Spring Boot App


Test APIs using Postman

🎯 What This Project Demonstrates

Real-world backend development practices

Clean separation of concerns

Transaction management

Database consistency

Professional API error handling

📌 Future Enhancements

Authentication & Authorization (Spring Security)

Pagination and filtering

Swagger / OpenAPI documentation

Dockerization

CI/CD pipeline