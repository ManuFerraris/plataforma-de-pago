package com.mipasarela.repository;

import com.mipasarela.domain.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findByMerchantId(Long merchantId);
}