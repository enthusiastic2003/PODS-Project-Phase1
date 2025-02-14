package com.sirjanhansda.pods.controller;

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
