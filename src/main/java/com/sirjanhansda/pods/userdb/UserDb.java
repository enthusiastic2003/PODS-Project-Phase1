package com.sirjanhansda.pods.userdb;


import com.sirjanhansda.pods.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDb extends JpaRepository<Customer, Integer> {

    List<Customer> findCustomerById(int id);
    List<Customer> findCustomerByName(String name);
    List<Customer> findCustomerByEmail(String email);

}
