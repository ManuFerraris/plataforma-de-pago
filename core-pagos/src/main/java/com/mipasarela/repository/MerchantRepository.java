package com.mipasarela.repository;

import com.mipasarela.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    
    Optional<Merchant> findByCuit(String cuit);
    Optional<Merchant> findByApiKey(String apiKey);
}
