package com.sirjanhansda.pods.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class StartUpTasks {

    @Autowired
    private CSVProdReader csvProdReader;

    @PostConstruct
    public void init() {
        Boolean transactionStatus = csvProdReader.loadProductsFromCsv("products.csv");

        if(!transactionStatus) {
            System.out.println("No products were found in CSV/ Read Error");
        }
        else {
            System.out.println("Successfully read products from CSV");
        }
    }
}