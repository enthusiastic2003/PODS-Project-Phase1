import requests
import sys

userServiceURL = "http://localhost:8080"
marketplaceServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"


def create_user(userId, name, email):
    new_user = {"id": userId, "name": name, "email": email}
    return requests.post(userServiceURL + "/users", json=new_user)

def get_user(user_id):
    return requests.get(userServiceURL + f"/users/{user_id}")

def get_wallet(user_id):
    return requests.get(walletServiceURL + f"/wallets/{user_id}")

def update_wallet(user_id, action, amount):
    return requests.put(walletServiceURL + f"/wallets/{user_id}", 
                       json={"action": action, "amount": amount})

def get_product_details(product_id):
    return requests.get(marketplaceServiceURL + f"/products/{product_id}")

def create_order(user_id, items):
    new_order = {
        "user_id": user_id,
        "items": items
    }
    return requests.post(marketplaceServiceURL + "/orders", json=new_order)

def delete_users():
    requests.delete(userServiceURL + "/users")

def get_orders(user_id):
    return requests.get(marketplaceServiceURL + f"/orders/users/{user_id}")

def return_order_byuser(user_id):
    return requests.delete(marketplaceServiceURL + f"/marketplace/users/{user_id}")