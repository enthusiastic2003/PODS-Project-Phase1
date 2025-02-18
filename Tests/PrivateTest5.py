# %%
import primary_functions as pf
import requests

# Start Debugging
print("==== STARTING TEST SCENARIO ====")

# %%
print("\n[STEP 1] Creating User...")
resp = pf.create_user(101, "John Doe", "jd@gm")
print(f"Response: {resp.status_code} - {resp.text}")
assert resp.status_code == 201, "User creation failed"

# %%
print("\n[STEP 2] Crediting Wallet with 1,000,000...")
resp2 = pf.update_wallet(101, "credit", 1000000)
print(f"Response: {resp2.status_code} - {resp2.text}")
assert resp2.status_code == 200, "Wallet credit failed"

# %%
print("\n[STEP 3] Attempting to Debit an Excessive Amount (10,000,000)...")
resp3 = pf.update_wallet(101, "debit", 10000000)
print(f"Response: {resp3.status_code} - {resp3.text}")
assert resp3.status_code == 400, "Debit should have failed due to insufficient balance"

# %%
print("\n[STEP 4] Fetching Wallet Balance...")
wallet = pf.get_wallet(101).json()
print(f"Wallet Data: {wallet}")
assert wallet["balance"] == 1000000, "Wallet balance mismatch after failed debit"

# %%
print("\n[STEP 5] Fetching Product Details...")
prods = pf.get_product_details(101).json()
p101 = prods["price"]
print(f"Product 101 Price: {p101}")

p102 = pf.get_product_details(102).json()["price"]
print(f"Product 102 Price: {p102}")

# %%
print("\n[STEP 6] Placing First Order with Discount...")
items = [{"product_id": 101, "quantity": 3}, {"product_id": 102, "quantity": 2}]
orderReceipt = pf.create_order(101, items)
print(f"Order Response: {orderReceipt.status_code} - {orderReceipt.text}")
assert orderReceipt.status_code == 201, "Order creation failed"

# %%
print("\n[STEP 7] Checking Total Cost with 10% Discount...")
totalCost = orderReceipt.json()["total_price"]
expectedCost = (3 * p101 + 2 * p102) * 0.9
print(f"Expected Total: {expectedCost}, Actual: {totalCost}")
assert totalCost == expectedCost, "Discount calculation error"

# %%
print("\n[STEP 8] Checking Wallet Balance After First Order...")
currentWalletOrder1 = pf.get_wallet(101).json()
print(f"Wallet After Order: {currentWalletOrder1}")
assert currentWalletOrder1["balance"] == 1000000 - totalCost, "Wallet deduction incorrect"

# %%
print("\n[STEP 9] Placing Second Order (No Discount)...")
itmes2 = [{"product_id": 103, "quantity": 2}]
p103 = pf.get_product_details(103).json()["price"]
print(f"Product 103 Price: {p103}")

orderReceipt2 = pf.create_order(101, itmes2)
print(f"Order Response: {orderReceipt2.status_code} - {orderReceipt2.text}")
assert orderReceipt2.status_code == 201, "Second order creation failed"

# %%
print("\n[STEP 10] Checking Total Cost of Second Order...")
totalCost2 = orderReceipt2.json()["total_price"]
expectedCost2 = 2 * p103
print(f"Expected Total: {expectedCost2}, Actual: {totalCost2}")
assert totalCost2 == expectedCost2, "Second order price calculation error"

# %%
print("\n[STEP 11] Checking Wallet Balance After Second Order...")
current_wallet = pf.get_wallet(101).json()
print(f"Wallet After Second Order: {current_wallet}")
assert current_wallet["balance"] == 1000000 - totalCost - totalCost2, "Wallet balance incorrect after second order"

# %%
print("\n[STEP 12] Fetching All Orders for User...")
all_orders = pf.get_orders(101)
print(f"Orders: {all_orders.json()}")

# %%
print("\n[STEP 13] Canceling All Orders for User...")
delOrder = pf.return_order_byuser(101)
print(f"Cancel Response: {delOrder}")

# %%
print("\n[STEP 14] Checking Wallet After Order Cancellations...")
wallet_after_cancel = pf.get_wallet(101).json()
print(f"Wallet After Cancelation: {wallet_after_cancel}")

# %%
print("\n[STEP 15] Checking Orders After Cancellations...")
orders_after_cancel = pf.get_orders(101).json()
print(f"Orders After Cancelation: {orders_after_cancel}")

# %%
print("\n[STEP 17] Deleting User Account...")
resp = requests.delete(pf.userServiceURL + "/users/101")
print(f"Delete Response: {resp.status_code} - {resp.text}")
assert resp.status_code == 200, "User deletion failed"

# %%
print("\n[STEP 18] Checking Wallet After Deletion...")
wallet_check = pf.get_wallet(101)
print(f"Wallet Check Response: {wallet_check}")
assert wallet_check.status_code == 404, "Wallet data should not be found after user deletion"

# %%
print("\n[STEP 19] Checking User Data After Deletion...")
user_check = pf.get_user(101)
print(f"User Check Response: {user_check}")
assert user_check.status_code == 404, "User data should not be found after deletion"

# %%
print("\n==== TEST SCENARIO COMPLETED SUCCESSFULLY ====")
