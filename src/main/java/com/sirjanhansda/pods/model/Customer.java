package com.sirjanhansda.pods.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
public class Customer {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Integer id;

    private String name;
    private String email;


    private Boolean discount_availed = false;



};
