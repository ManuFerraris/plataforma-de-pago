package com.mipasarela.repository.memory;

import com.mipasarela.domain.Transaction;
import com.mipasarela.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionMemoryRepository implements TransactionRepository {

    private final Map<Long, Transaction> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getTransactionId() == null) {
            // Asignamos una PK auto-incrementada si es un registro nuevo
            transaction.setTransactionId(idSequence.getAndIncrement());
        }
        storage.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null)
            return Optional.empty();

        return storage.values().stream()
                .filter(tx -> idempotencyKey.equals(tx.getIdempotencyKey()))
                .findFirst();
    }

    @Override
    public List<Transaction> findByMerchantId(Long merchantId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : storage.values()) {
            if (tx.getMerchant() != null && merchantId.equals(tx.getMerchant().getMerchantId())) {
                result.add(tx);
            }
        }
        return result;
    }
}