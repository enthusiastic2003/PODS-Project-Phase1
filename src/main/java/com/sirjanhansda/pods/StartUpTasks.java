package com.sirjanhansda.pods;

import com.sirjanhansda.pods.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartUpTasks {

    private final CSVProdReader csvProdReader = new CSVProdReader();

    @PostConstruct
    public void init() {
        Boolean TransactionStatus = csvProdReader.loadProductsFromCsv("products.csv");

        if(!TransactionStatus) {
            System.out.println("No products were found in CSV/ Read Error");
        }
    }

}
