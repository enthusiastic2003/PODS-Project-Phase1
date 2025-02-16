package com.sirjanhansda.pods.products.controller;


import com.sirjanhansda.pods.products.model.OrderItem;
import com.sirjanhansda.pods.products.model.OrderStatus;
import com.sirjanhansda.pods.products.model.Orders;
import com.sirjanhansda.pods.products.model.Product;
import com.sirjanhansda.pods.products.orderdb.OrdersDb;
import com.sirjanhansda.pods.products.proddb.ProdDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/marketplace")
public class MarketPlaceRouter {

    @Value("${account.service.url}")
    private String accountServiceUrl;

    @Value("${wallets.service.url}")
    private String walletServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private OrdersDb ordersDb;

    @Autowired
    private ProdDb prodDb;

    @DeleteMapping("/users/{userid}")
    public ResponseEntity<?> deleteOrderByUserId(@PathVariable("userid") Integer userid) {

        List<Orders> ordersWithUserId = ordersDb.findOrdersByUser_id(userid);

        if (ordersWithUserId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<OrderItem>  orderItems =new ArrayList<>();

        Integer totalRefundablePrice = 0;
        for (Orders order : ordersWithUserId) {
            if(order.getStatus().equals(OrderStatus.PLACED)) {
                order.setStatus(OrderStatus.CANCELLED);
                ordersDb.save(order);
                totalRefundablePrice+= order.getTotal_price();
                orderItems.addAll(order.getItems());
            }


        }

        boolean restoreDone = restoreBalance(userid, totalRefundablePrice);

        if (restoreDone) {
            System.out.println("Restore done");
        }
        else{
            System.out.println("Restore failed");
        }

        restoreStock(orderItems);



        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteAllOrders() {
        List<Orders> allOrders = ordersDb.findAll();
        if (allOrders.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        Map<Integer, Integer> refundableAmounts = new HashMap<>();
        List<OrderItem> orderItems = new ArrayList<>();

        for (Orders order : allOrders) {
            if (order.getStatus().equals(OrderStatus.PLACED)) {
                order.setStatus(OrderStatus.CANCELLED);
                ordersDb.save(order);

                refundableAmounts.put(order.getUser_id(),
                        refundableAmounts.getOrDefault(order.getUser_id(), 0) + order.getTotal_price()
                );

                orderItems.addAll(order.getItems());
            }
        }

        for (Map.Entry<Integer, Integer> entry : refundableAmounts.entrySet()) {
            boolean restoreDone = restoreBalance(entry.getKey(), entry.getValue());
            System.out.println("Restore for user " + entry.getKey() + (restoreDone ? " done" : " failed"));
        }

        restoreStock(orderItems);

        return ResponseEntity.ok().build();
    }


    private boolean restoreBalance(Integer userid, Integer price){


        WalletPUTRequest walletPutRequest = new WalletPUTRequest();
        walletPutRequest.setAmount(price);
        walletPutRequest.setAction(WalletPUTRequest.Action.credit);

        ResponseEntity<?> response;

        try {
            response = restTemplate.exchange(walletServiceUrl + "/wallets/" + userid, HttpMethod.PUT, new HttpEntity<>(walletPutRequest), String.class);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        return response.getStatusCode() == HttpStatus.OK;



    }

    private void restoreStock(List<OrderItem> restoreItems) {

        for (OrderItem restoreItem : restoreItems) {

            Integer quantity = restoreItem.getQuantity();
            Integer prodId = restoreItem.getProduct_id();

            List<Product> products =  prodDb.findProductById(prodId);


            Product product = products.get(0);

            product.setStock_quantity(product.getStock_quantity()+quantity);

            prodDb.save(product);

        }

    }
}
