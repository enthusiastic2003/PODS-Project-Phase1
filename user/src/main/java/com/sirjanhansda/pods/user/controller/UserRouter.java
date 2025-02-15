package com.sirjanhansda.pods.user.controller;

import com.sirjanhansda.pods.user.model.Customer;
import com.sirjanhansda.pods.user.userdb.UserDb;
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

    @PutMapping("/{usrid}")
    public ResponseEntity<?> updateUserDiscountStatus(@PathVariable final Integer usrid, @RequestBody final Boolean discountStatus) {
        List<Customer> customerLists = userDb.findCustomerById(usrid);

        

        if (customerLists.isEmpty()) {
            return ResponseEntity.notFound().build();

        }
        else {
            System.out.println(discountStatus);
            Customer customer = customerLists.get(0);
            customer.setDiscount_availed(discountStatus);
            try {
                userDb.save(customer);
            }
            catch (Exception e) {
                return ResponseEntity.internalServerError().body(e.getMessage());
            }

            return ResponseEntity.ok().build();
        }

    }

    @DeleteMapping("/{usrid}")
    public ResponseEntity<?> deleteUser(@PathVariable final Integer usrid) {
        List<Customer> customerLists = userDb.findCustomerById(usrid);
        if (customerLists.isEmpty()) {

            return ResponseEntity.notFound().build();
        }
        else {
            Customer customer = customerLists.get(0);

            try {
                userDb.delete(customer);
            }
            catch (Exception e) {
                return ResponseEntity.internalServerError().body(e.getMessage());
            }
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<?> deleteAllUsers() {
        try {
            userDb.deleteAll();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}

