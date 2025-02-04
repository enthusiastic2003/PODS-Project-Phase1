package com.sirjanhansda.pods.controller;

import com.sirjanhansda.pods.model.Customer;
import com.sirjanhansda.pods.userdb.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")  // Base URL for all endpoints in this controller
public class UserRouter {

    @Autowired
    private UserDb userDb;

    // Add user - POST /users
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody final Customer customer) {

        String custEmail = customer.getEmail();

        // Check if the email already exists
        List<Customer> existingCustomer = userDb.findCustomerByEmail(custEmail);

        if(existingCustomer.isEmpty()) {
            Customer savedCustomer = userDb.save(customer);
            return ResponseEntity.ok(savedCustomer);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get user by ID - GET /users/{usrid}
    @GetMapping("/{usrid}")
    public ResponseEntity<?> getUser(@PathVariable final Integer usrid) {

        // Find customer by ID
        List<Customer> customerLists = userDb.findCustomerById(usrid);

        if (customerLists.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(customerLists.get(0));
    }
}
