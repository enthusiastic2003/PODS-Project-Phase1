package com.sirjanhansda.pods.controller;


import com.sirjanhansda.pods.model.Customer;
import com.sirjanhansda.pods.userdb.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class Router {

    @Autowired
    private UserDb userDb;

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody final Customer customer) {

        String custEmail = customer.getEmail();

        List<Customer> existingCustomer = userDb.findCustomerByEmail(custEmail);

        if(existingCustomer.isEmpty()) {
            Customer svdCustomer = userDb.save(customer);
            return ResponseEntity.ok(svdCustomer);
        }
        else {
            return  ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{usrid}")
    public ResponseEntity<?> getUser(@PathVariable final Integer usrid) {

        List<Customer> customerLists = userDb.findCustomerById(usrid);

        if (customerLists.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return  ResponseEntity.ok(customerLists.get(0));
    }




}