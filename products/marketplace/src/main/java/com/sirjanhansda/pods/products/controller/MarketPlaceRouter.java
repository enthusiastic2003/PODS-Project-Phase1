package com.sirjanhansda.pods.products.controller;


import com.sirjanhansda.pods.products.model.OrderStatus;
import com.sirjanhansda.pods.products.model.Orders;
import com.sirjanhansda.pods.products.orderdb.OrdersDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/marketplace")
public class MarketPlaceRouter {

    @Autowired
    private OrdersDb ordersDb;

    @DeleteMapping("/users/{userid}")
    public ResponseEntity<?> deleteOrderByUserId(@PathVariable("userid") Integer userid) {

        List<Orders> ordersWithUserId = ordersDb.findOrdersByUser_id(userid);

        if (ordersWithUserId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        for (Orders order : ordersWithUserId) {
            order.setStatus(OrderStatus.CANCELLED);
            ordersDb.save(order);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteAllOrders() {
        List<Orders> allOrders = ordersDb.findAll();
        if (allOrders.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        for (Orders order : allOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            ordersDb.save(order);
        }
        return ResponseEntity.ok().build();
    }
}
