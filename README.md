# 🏦 Bank Account Management System — REST API

A production-style RESTful API built with **Java 21, Spring Boot 3, MySQL**, simulating core banking operations including account management, deposits, withdrawals, fund transfers, and transaction history.

> Built as a portfolio project targeting enterprise banking software roles (Finacle / EdgeVerve / Infosys).

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 |
| Testing | JUnit 5 + Mockito |
| API Docs | Swagger / OpenAPI 3 |
| Tools | Postman, Git, Maven |

---

## 🚀 Features

- ✅ Create bank account (SAVINGS / CURRENT)
- ✅ Deposit money
- ✅ Withdraw money (with insufficient funds validation)
- ✅ Transfer funds between accounts (with self-transfer prevention)
- ✅ View transaction history
- ✅ Deactivate account
- ✅ Structured error handling for all business rule violations
- ✅ Input validation with meaningful error messages
- ✅ JUnit unit tests for all service-layer business logic
- ✅ Swagger UI for interactive API documentation

---

## ⚡ Quick Start

### 1. Prerequisites
- Java 21
- MySQL 8
- Maven

### 2. Database Setup
```sql
CREATE DATABASE bank_db;
```

### 3. Configure DB credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 4. Run the app
```bash
mvn spring-boot:run
```

### 5. Open Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 📋 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts` | Create new account |
| GET | `/api/accounts` | Get all active accounts |
| GET | `/api/accounts/{accNo}` | Get account details |
| POST | `/api/accounts/{accNo}/deposit` | Deposit money |
| POST | `/api/accounts/{accNo}/withdraw` | Withdraw money |
| POST | `/api/accounts/{accNo}/transfer` | Transfer to another account |
| GET | `/api/accounts/{accNo}/transactions` | Transaction history |
| DELETE | `/api/accounts/{accNo}` | Deactivate account |

---

## 🧪 Sample Requests (Postman)

### Create Account
```json
POST /api/accounts
{
  "accountHolderName": "Moheed Nawaaz",
  "email": "moheed@example.com",
  "accountType": "SAVINGS",
  "initialDeposit": 5000
}
```

### Deposit
```json
POST /api/accounts/ACC123456/deposit
{
  "amount": 1000,
  "description": "Salary credit"
}
```

### Transfer
```json
POST /api/accounts/ACC123456/transfer
{
  "toAccountNumber": "ACC789012",
  "amount": 2000,
  "description": "Rent payment"
}
```

---

## ❌ Error Scenarios Handled

| Scenario | HTTP Status | Response |
|---|---|---|
| Account not found | 404 | Account not found: ACC999 |
| Insufficient funds | 400 | Insufficient funds. Available: ₹500, Requested: ₹1000 |
| Inactive account | 403 | Account ACC123 is not active |
| Duplicate email | 409 | Account with email already exists |
| Self-transfer | 400 | Cannot transfer funds to the same account |
| Invalid input | 400 | Field-level validation errors |

---

## 🧪 Running Tests
```bash
mvn test
```

---

## 📁 Project Structure
```
src/
├── main/java/com/moheed/bankapi/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # JPA data access
│   ├── model/          # Account, Transaction entities
│   ├── dto/            # Request/Response objects
│   └── exception/      # Custom exceptions + Global handler
└── test/
    └── service/        # JUnit unit tests
```
