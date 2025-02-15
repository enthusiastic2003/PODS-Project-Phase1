package com.sirjanhansda.pods.products.controller;

import com.sirjanhansda.pods.products.model.*;
import com.sirjanhansda.pods.products.orderdb.OrdersDb;
import com.sirjanhansda.pods.products.proddb.ProdDb;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            return ResponseEntity.badRequest().body("Failed to process payment");
        }

        // Reduce stock quantity
        boolean stockUpdated = updateStockLevels(prodPOSTRequest);
        if (!stockUpdated) {
            return ResponseEntity.badRequest().body("Failed to update stock levels");
        }

        boolean discountUpdated = updateDiscountStatus(prodPOSTRequest, Objects.requireNonNull(customerResponse.getBody()));


        if (!discountUpdated) {
            return ResponseEntity.badRequest().body("Failed to update discount status");
        }

        Orders ord = createOrder(prodPOSTRequest, userId, totalCost);




        return ResponseEntity.status(HttpStatus.CREATED).body(ord);
    }


    @GetMapping("/{orderid}")
    public ResponseEntity<?> getOrders(@PathVariable Integer orderid) {

        List<Orders> ordersWithId = ordersDb.findOrdersByOrder_id(orderid);

        if (ordersWithId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ordersWithId.get(0));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Integer userId) {

        List<Orders> ordersWithId = ordersDb.findOrdersByUser_id(userId);
        if (ordersWithId.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
        return ResponseEntity.ok().body(ordersWithId.get(0));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> setOrderStatus(@PathVariable Integer orderId, @RequestBody OrderPUTStatus orderPUTStatus) {

        Integer orderIdReq = orderPUTStatus.getOrder_id();

        if(!Objects.equals(orderIdReq, orderId)) {
            return ResponseEntity.badRequest().body("Requested order id is not equal to requested order id");
        }

        List<Orders> ordersWithId = ordersDb.findOrdersByOrder_id(orderId);
        if (ordersWithId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Orders order = ordersWithId.get(0);

        if(order.getStatus() != OrderStatus.PLACED) {
            return ResponseEntity.badRequest().body("Order is not placed");
        }

        order.setStatus(orderPUTStatus.getStatus());

        ordersDb.save(order);

        return ResponseEntity.ok().build();


    }

    @GetMapping()
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok().body(ordersDb.findAll());
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

    private boolean updateDiscountStatus(ProdPOSTRequest prodPOSTRequest, Customer customer) {
        Integer userId = prodPOSTRequest.getUser_id();

        if (!customer.getDiscount_availed()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Boolean> requestEntity = new HttpEntity<>(true, headers);

            ResponseEntity<?> updateDiscount;
            try{
                restTemplate.put(accountServiceUrl + "/users/" + userId, requestEntity);
            }
            catch (HttpClientErrorException.NotFound e) {
                System.out.println(e.getMessage());
                return false;
            }


        }

        return true;

    }

    @Transactional
    public Orders createOrder(ProdPOSTRequest prodPOSTRequest, Integer userId, double totalCost) {
        Orders order = new Orders();
        order.setUser_id(userId);
        order.setStatus(OrderStatus.PLACED);
        order.setTotal_price((int) totalCost);

        List<OrderItem> orderItems = new ArrayList<>();

        for(ItemFormat orderItem: prodPOSTRequest.getItems()){

            OrderItem orderItem1 = new OrderItem();
            orderItem1.setQuantity(orderItem.getQuantity());
            orderItem1.setProduct_id(orderItem.getProduct_id());
            orderItem1.setOrder(order);  // 👈 Important: Link item to order
            orderItems.add(orderItem1);


        }

        order.setItems(orderItems);

        ordersDb.save(order);

        return order;
    }
}
