package com.sirjanhansda.pods.products;

import com.opencsv.CSVReader;
import com.sirjanhansda.pods.products.model.Product;
import com.sirjanhansda.pods.products.proddb.ProdDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVProdReader implements CommandLineRunner {

    @Autowired
    private ProdDb prodDb;

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            System.err.println("Usage: Provide the path to the CSV file as an argument.");
            return;
        }
        String filePath = args[0]; // Get the file path from command-line arguments
        loadProductsFromCsv(filePath);
    }

    Boolean loadProductsFromCsv(String filename) {
        List<Product> products = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(filename); // Use FileInputStream to read from any location
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext(); // Skip header
            if (header == null) {
                throw new RuntimeException("Empty CSV file");
            }

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                try {
                    Product product = Product.builder()
                            .id(Integer.parseInt(nextLine[0]))
                            .name(nextLine[1])
                            .description(nextLine[2])
                            .price(Integer.parseInt(nextLine[3]))
                            .stock_quantity(Integer.parseInt(nextLine[4]))
                            .build();
                    products.add(product);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid product data: " + String.join(", ", nextLine), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load products from CSV: " + e.getMessage(), e);
        }

        if (products.isEmpty()) {
            throw new RuntimeException("No products found in CSV file");
        }

        prodDb.saveAll(products);
        return true;
    }
}