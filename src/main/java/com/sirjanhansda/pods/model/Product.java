package com.sirjanhansda.pods.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Data
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
