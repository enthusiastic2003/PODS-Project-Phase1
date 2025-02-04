package com.sirjanhansda.pods.controller;

import com.sirjanhansda.pods.model.Product;
import com.sirjanhansda.pods.proddb.ProdDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProdRouter {

    @Autowired
    private ProdDb prodDb;

    @GetMapping
    public ResponseEntity<?> getProducts() {

        return ResponseEntity.ok(prodDb.findAll());

    }

    @GetMapping("/{prodId}")
    public ResponseEntity<?> getProduct(@PathVariable("prodId") final Integer prodId) {

        List<Product> ProdByProdId = prodDb.findProductById(prodId);

        if (ProdByProdId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(ProdByProdId);
        }
    }

}
