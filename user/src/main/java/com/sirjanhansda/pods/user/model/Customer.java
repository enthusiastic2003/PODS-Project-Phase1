package com.sirjanhansda.pods.user.model;

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
public class Customer {

    @Id
    private Integer id;

    private String name;
    private String email;


    private Boolean discount_availed = false;



};
