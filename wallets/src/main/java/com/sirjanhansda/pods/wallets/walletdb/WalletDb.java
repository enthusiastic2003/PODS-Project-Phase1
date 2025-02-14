package com.sirjanhansda.pods.wallets.walletdb;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sirjanhansda.pods.wallets.model.UsrWallet;

import java.util.List;

@Repository
public interface WalletDb extends JpaRepository<UsrWallet, Integer>{

    List<UsrWallet> findUsrWalletByUserid(Integer userid);

    
}
