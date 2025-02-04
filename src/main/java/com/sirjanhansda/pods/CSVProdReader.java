package com.sirjanhansda.pods;

import com.opencsv.CSVReader;
import com.sirjanhansda.pods.model.Product;
import com.sirjanhansda.pods.proddb.ProdDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVProdReader {

    @Autowired
    public ProdDb prodDb;

    public Boolean loadProductsFromCsv(String filename) {
        List<Product> products = new ArrayList<>();

        // Get the class loader
        ClassLoader classLoader = getClass().getClassLoader();

        // Open the file as an InputStream
        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.out.println("File not found: " + filename);
                return false;
            }

            // Wrap the InputStream with InputStreamReader for CSVReader
            try (InputStreamReader reader = new InputStreamReader(inputStream);
                 CSVReader csvReader = new CSVReader(reader)) {

                String[] nextLine;
                csvReader.readNext(); // Skip the header line

                while ((nextLine = csvReader.readNext()) != null) {
                    try {
                        // Parse the CSV fields
                        Integer id = Integer.parseInt(nextLine[0]);
                        String name = nextLine[1];
                        String description = nextLine[2];
                        Integer price = Integer.parseInt(nextLine[3]);
                        Integer quantity = Integer.parseInt(nextLine[4]);

                        // Create a new product and add it to the list
                        Product product = new Product(id, name, description, price, quantity);
                        products.add(product);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid product entry: " + String.join(", ", nextLine));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error reading CSV file: " + e.getMessage());
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error opening file: " + e.getMessage());
            return false;
        }

        // Check if products list is not empty before saving
        if (!products.isEmpty()) {
            prodDb.saveAll(products); // Save all products to the database
            return true;
        } else {
            System.out.println("No valid products to save.");
            return false;
        }
    }
}
