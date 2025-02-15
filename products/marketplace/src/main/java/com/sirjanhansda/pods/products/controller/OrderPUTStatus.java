package com.sirjanhansda.pods.products.controller;

import com.sirjanhansda.pods.products.model.OrderStatus;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class OrderPUTStatus {
    private Integer order_id;

    @Enumerated
    private OrderStatus status;
}
