package com.sirjanhansda.pods.products.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
class ItemFormat{

    Integer product_id;
    Integer quantity;

}

@Getter
@Setter
public class ProdPOSTRequest {

    Integer user_id;
    List<ItemFormat> items;

}
