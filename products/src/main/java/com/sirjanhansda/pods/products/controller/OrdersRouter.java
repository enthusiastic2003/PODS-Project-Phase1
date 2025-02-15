package com.sirjanhansda.pods.products.controller;

import com.sirjanhansda.pods.products.model.Customer;
import com.sirjanhansda.pods.products.model.Product;
import com.sirjanhansda.pods.products.model.UsrWallet;
import com.sirjanhansda.pods.products.orderdb.OrdersDb;
import com.sirjanhansda.pods.products.proddb.ProdDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersRouter {

    @Autowired
    private OrdersDb ordersDb;

    @Autowired
    private ProdDb prodDb;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    @Value("${wallets.service.url}")
    private String walletServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<?> takeOrder(@RequestBody ProdPOSTRequest prodPOSTRequest) {
        Integer userId = prodPOSTRequest.getUser_id();

        // Fetch user details
        ResponseEntity<Customer> customerResponse = getCustomerDetails(userId);
        if (!customerResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Calculate total order cost
        double totalCost = calculateTotalCost(prodPOSTRequest, customerResponse.getBody());
        if (totalCost < 0) {
            return ResponseEntity.badRequest().body("Product not found or stock insufficient");
        }

        // Fetch user wallet details
        ResponseEntity<UsrWallet> walletResponse = getWalletDetails(userId);
        if (!walletResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().body("User wallet not found");
        }

        UsrWallet usrWallet = walletResponse.getBody();

        // Check if user has enough balance
        if (usrWallet.getBalance() < totalCost) {
            return ResponseEntity.badRequest().body("Not enough money");
        }

        // Debit the amount from wallet
        boolean debitSuccess = debitAmountFromWallet(userId, totalCost);
        if (!debitSuccess) {
            return ResponseEntity.internalServerError().body("Failed to process payment");
        }

        // Reduce stock quantity
        boolean stockUpdated = updateStockLevels(prodPOSTRequest);
        if (!stockUpdated) {
            return ResponseEntity.internalServerError().body("Failed to update stock levels");
        }

        return ResponseEntity.ok().body("Order placed successfully");
    }

    private ResponseEntity<Customer> getCustomerDetails(Integer userId) {
        try {
            return restTemplate.getForEntity(accountServiceUrl + "/users/" + userId, Customer.class);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private double calculateTotalCost(ProdPOSTRequest prodPOSTRequest, Customer customer) {
        double totalCost = 0;
        for (ItemFormat item : prodPOSTRequest.getItems()) {
            List<Product> products = prodDb.findProductById(item.getProduct_id());
            if (products.isEmpty()) {
                return -1; // Indicates product not found
            }

            Product product = products.get(0);
            if (product.getStock_quantity() < item.getQuantity()) {
                return -1; // Indicates insufficient stock
            }

            // Apply discount if applicable
            double discountFactor = customer.getDiscount_availed() ? 1.0 : 0.9;
            totalCost += discountFactor * product.getPrice() * item.getQuantity();
        }
        return totalCost;
    }

    private ResponseEntity<UsrWallet> getWalletDetails(Integer userId) {
        try {
            return restTemplate.getForEntity(walletServiceUrl + "/wallets/" + userId, UsrWallet.class);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean debitAmountFromWallet(Integer userId, double amount) {
        try {
            String url = walletServiceUrl + "/wallets/" + userId;

            WalletPUTRequest request = new WalletPUTRequest();
            request.setAction(WalletPUTRequest.Action.debit);
            request.setAmount((int) amount);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WalletPUTRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean updateStockLevels(ProdPOSTRequest prodPOSTRequest) {
        for (ItemFormat item : prodPOSTRequest.getItems()) {
            List<Product> products = prodDb.findProductById(item.getProduct_id());


            Product product = products.get(0);


            product.setStock_quantity(product.getStock_quantity() - item.getQuantity());
            prodDb.save(product);
        }
        return true;
    }
}
