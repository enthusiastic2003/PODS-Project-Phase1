package com.sirjanhansda.pods.controller;


import com.sirjanhansda.pods.model.Customer;
import com.sirjanhansda.pods.model.Product;
import com.sirjanhansda.pods.model.UsrWallet;
import com.sirjanhansda.pods.orderdb.OrdersDb;
import com.sirjanhansda.pods.proddb.ProdDb;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersRouter {

    @Autowired
    private OrdersDb ordersDb;

    @Autowired
    private ProdDb prodDb;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    RestTemplate restTemplate;


    @PostMapping("/")
    ResponseEntity<?> takeOrder(@RequestBody ProdPOSTRequest prodPOSTRequest) {

        Integer userId = prodPOSTRequest.getUser_id();

        ResponseEntity<Customer> response;

        try {

            response = restTemplate.postForEntity(accountServiceUrl + "/users/" + userId, null, Customer.class);

        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body("User not found");
        }

        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        double totalCost = 0;

        for(ItemFormat item : prodPOSTRequest.getItems()) {

            List<Product> products = prodDb.findProductById(item.getProduct_id());
            if(products.isEmpty()){
                return ResponseEntity.badRequest().body("Product not found");
            }

            Product product = products.get(0);

            if(product.getStock_quantity()<item.getQuantity()) {
                return ResponseEntity.badRequest().body("Stock quantity exceeded");
            }

            totalCost += response.getBody().getDiscount_availed() ?
                    item.getQuantity()*product.getPrice(): 0.9 * product.getPrice()*item.getQuantity();

        }

            ResponseEntity<UsrWallet> walletResponse;
        try {

                walletResponse = restTemplate.postForEntity(accountServiceUrl + "/wallets/" + userId, null, UsrWallet.class);
        }
        catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body("User not found");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        UsrWallet usrWallet = walletResponse.getBody();

        if(usrWallet.getBalance()<totalCost) {
            return ResponseEntity.badRequest().body("Not enough money");
        }


        try {

            RequestEntity

        }



    }
}
