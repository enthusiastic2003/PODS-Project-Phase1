package com.sirjanhansda.pods.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Order
{
    @Id private Integer id;
    private Integer user_id;
    private Integer total_price;
    private String status;
}
