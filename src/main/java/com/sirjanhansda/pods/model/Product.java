package com.sirjanhansda.pods.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;



@Entity
@Data
@Builder
public class Product {

    @Id
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock_quantity;

    public Product(Integer id, String name, String description, Integer price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock_quantity = quantity;
        this.price = price;
    }

    public Product() {

    }
}
