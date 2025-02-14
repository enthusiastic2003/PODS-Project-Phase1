package com.sirjanhansda.pods.orderdb;

import com.sirjanhansda.pods.model.OrderStatus;
import com.sirjanhansda.pods.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersDb extends JpaRepository<Orders, Integer> {

    List<Orders> findOrdersById(Integer id);
    List<Orders> findOrdersByUserid(Integer id);
    List<Orders> findOrdersByStatus(OrderStatus status);
}
