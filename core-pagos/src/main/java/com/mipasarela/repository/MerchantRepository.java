package com.mipasarela.repository;

import java.util.Optional;

import com.mipasarela.domain.Merchant;

public interface MerchantRepository {
    Merchant save(Merchant merchant);

    Optional<Merchant> findById(Long id);

    Optional<Merchant> findByCuit(String cuit);
}
