package com.sirjanhansda.pods.products.controller;

import jakarta.persistence.Enumerated;
import lombok.Setter;



@Setter
public class WalletPUTRequest{

    public enum Action {
        debit,
        credit
    }

    @Enumerated
    public Action action;
    public Integer amount;
    
}
