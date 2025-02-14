package com.sirjanhansda.pods.wallets.controller;

import com.sirjanhansda.pods.wallets.model.UsrWallet;
import com.sirjanhansda.pods.wallets.walletdb.WalletDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletRouter {



    @Autowired
    private WalletDb walletDb;

    @GetMapping("/{usrid}")
    public ResponseEntity<?> getWallet(@PathVariable Integer usrid) {

        List<UsrWallet> usrWalletList = walletDb.findUsrWalletByUserid(usrid);

        if (usrWalletList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(usrWalletList.get(0));
        }
    }

    @PutMapping("/{usrid}")
    public ResponseEntity<?> updateWallet(@PathVariable Integer usrid, @RequestBody WalletPUTRequest walletPUTRequest) {

        List<UsrWallet> usrWalletList = walletDb.findUsrWalletByUserid(usrid);

        UsrWallet newUsrWallet;
        if (usrWalletList.isEmpty()) {

            newUsrWallet = new UsrWallet();
            newUsrWallet.setUserid(usrid);
            newUsrWallet.setBalance(0);

        }
        else {
            newUsrWallet = usrWalletList.get(0);
        }

        try {
            if (walletPUTRequest.action == WalletPUTRequest.Action.debit) {
                if (newUsrWallet.getBalance() < walletPUTRequest.amount) {
                    return ResponseEntity.badRequest().body("Insufficient funds");
                }
                newUsrWallet.setBalance(newUsrWallet.getBalance() - walletPUTRequest.amount);
            } else if (walletPUTRequest.action == WalletPUTRequest.Action.credit) {
                if (newUsrWallet.getBalance() > Integer.MAX_VALUE - walletPUTRequest.amount) {
                    return ResponseEntity.badRequest().body("Amount too large");
                }
                newUsrWallet.setBalance(newUsrWallet.getBalance() + walletPUTRequest.amount);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Operation failed: " + e.getMessage());
        }


        walletDb.save(newUsrWallet);

        return ResponseEntity.ok(newUsrWallet);


    }

    @DeleteMapping("/{usrid}")
    public ResponseEntity<?> deleteWallet(@PathVariable Integer usrid) {

        List<UsrWallet> usrWalletList = walletDb.findUsrWalletByUserid(usrid);
        if (usrWalletList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            try {
                walletDb.delete(usrWalletList.get(0));
            }
            catch (Exception e) {
                return ResponseEntity.internalServerError().body("Operation failed: " + e.getMessage());
            }
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllWallets() {
        try {
            walletDb.deleteAll();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Operation failed: " + e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
