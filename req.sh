#!/bin/bash

# Create a user
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/users \
     -H "Content-Type: application/json" \
     -d '{"name": "shansda", "email": "hello3@gmail.com"}')

echo "User Response: $USER_RESPONSE"

# Extract user ID from response
USER_ID=$(echo $USER_RESPONSE | jq -r '.id')

echo "Extracted User ID: $USER_ID"

# Credit wallet for the user
WALLET_RESPONSE=$(curl -s -X PUT http://localhost:8081/wallets/$USER_ID \
     -H "Content-Type: application/json" \
     -d '{"action": "credit", "amount": 200000}')

echo "Wallet Response: $WALLET_RESPONSE"

# Place an order
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8082/orders \
     -H "Content-Type: application/json" \
     -d "{\"user_id\": $USER_ID, \"items\": [{\"product_id\": 103, \"quantity\": 1}]}")

echo "Order Response: $ORDER_RESPONSE"
