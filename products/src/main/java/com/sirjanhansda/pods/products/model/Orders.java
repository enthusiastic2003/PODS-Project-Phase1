package com.sirjanhansda.pods.products.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Orders
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private Integer userid;
    private Integer totalprice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
};
