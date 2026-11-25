# Inventory & Order Management System

Spring Boot backend for product inventory, customer management and order processing.

**Stack:** Java 17, Spring Boot 3, Spring Data JPA / Hibernate, Bean Validation, H2 (default) or MySQL, Maven

## Run
```bash
mvn spring-boot:run
```
API base: `http://localhost:8080/api` | H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:inventorydb`, user `sa`)

To use MySQL, edit `src/main/resources/application.properties` (instructions inside).

## Endpoints
| Method | Path | Description |
|---|---|---|
| GET/POST | `/api/customers` | list / create |
| GET/PUT/DELETE | `/api/customers/{id}` | read / update / delete |
| GET/POST | `/api/products` | list / create |
| GET/PUT/DELETE | `/api/products/{id}` | read / update / delete |
| GET/POST | `/api/orders` | list / place order |
| GET | `/api/orders/{id}` | read order |
| DELETE | `/api/orders/{id}` | cancel order (restores stock) |

## Postman examples
**Create customer** `POST /api/customers`
```json
{ "name": "Asha Patil", "email": "asha@example.com", "phone": "9876543210", "address": "Pune" }
```
**Create product** `POST /api/products`
```json
{ "name": "Wireless Mouse", "sku": "WM-100", "description": "2.4GHz", "price": 799.00, "stockQuantity": 50 }
```
**Place order** `POST /api/orders` (customer/product resolved by id, total auto-calculated)
```json
{ "customerId": 1, "items": [ { "productId": 1, "quantity": 2 } ] }
```

## Features
- CRUD REST APIs with JPA/Hibernate mappings (Customer 1-N Order 1-N OrderItem N-1 Product)
- Bean Validation on all request bodies, consistent JSON errors via `@RestControllerAdvice`
- CORS configured via `app.cors.allowed-origins`
- Order service: reference resolution, stock check and deduction, price snapshot, automatic total, cancellation with stock restore
