# Online Marketplace

## Overview
This project is an online marketplace where users can register, browse products, place orders, and make payments via a wallet system. The system consists of three microservices:

1. **Account Service** - Manages user accounts and discount eligibility.
2. **Marketplace Service** - Handles products, orders, and order management.
3. **Wallet Service** - Manages user wallet balances and transactions.

## Features
- User registration and account management
- Product catalog loaded from CSV
- Order placement with stock verification
- Wallet-based payment system
- 10% discount on the first order
- Order cancellation with stock and wallet refund
- Microservices architecture using Spring Boot
- Dockerized deployment

## System Architecture
This project follows a microservices architecture with three independent services communicating via REST APIs. The services are:

### 1. Account Service
- Registers and manages users
- Tracks discount eligibility
- Handles user deletion by notifying other services

### 2. Marketplace Service
- Reads product data from a CSV file at startup
- Manages orders and verifies stock availability
- Applies discounts and processes payments via Wallet Service
- Handles order cancellations and refunds

### 3. Wallet Service
- Stores user wallet balances
- Supports debit and credit operations
- Ensures sufficient balance before processing orders

## API Endpoints
### Account Service
- `POST /users` - Register a new user
- `GET /users/{userId}` - Get user details
- `DELETE /users/{userId}` - Delete a user
- `DELETE /users` - Reset all users

### Marketplace Service
- `GET /products` - List all products
- `GET /products/{productId}` - Get product details
- `POST /orders` - Place an order
- `GET /orders/{orderId}` - Get order details
- `DELETE /orders/{orderId}` - Cancel an order
- `PUT /orders/{orderId}` - Mark an order as delivered

### Wallet Service
- `GET /wallets/{userId}` - Get wallet balance
- `PUT /wallets/{userId}` - Perform a debit or credit transaction
- `DELETE /wallets/{userId}` - Delete a wallet
- `DELETE /wallets` - Reset all wallets

## Setup and Deployment
### Prerequisites
- Java 17+
- Maven
- Docker

### Steps to Run
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/online-marketplace.git
   cd online-marketplace
   ```
2. Build and run services using Docker:
   ```sh
   docker-compose up --build
   ```
3. Access the services at:
   - Account Service: `http://localhost:8080`
   - Marketplace Service: `http://localhost:8081`
   - Wallet Service: `http://localhost:8082`

## Testing
- Use Postman or `curl` to send requests to the API endpoints.
- Ensure correct responses for different scenarios (e.g., invalid user ID, insufficient wallet balance, etc.).
- A set of public test cases is provided for validation.

## Contribution
- Fork the repository
- Create a feature branch
- Commit changes with meaningful messages
- Open a pull request

## License
This project is licensed under the MIT License.

---
Developed as part of the **2025 Online Marketplace Project**.

