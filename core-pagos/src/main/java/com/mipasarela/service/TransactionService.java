package com.mipasarela.service;

import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Merchant;
import com.mipasarela.domain.Token;
import com.mipasarela.domain.Transaction;
import com.mipasarela.domain.TransactionStatus;
import com.mipasarela.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    // Inyección de dependencia a través de la Interfaz
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Procesar un intento de cobro aplicando las reglas de negocio.
    public Transaction processPayment(
            Merchant merchant,
            Customer customer,
            Token token,
            BigDecimal amount,
            String currency,
            String idempotencyKey) {
        // Regla 1: Control de Idempotencia (Esta transacción ya fue procesada?)
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Transaction> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existingTx.isPresent()) {
                // Devolvemos la transacción previamente procesada de forma inmediata
                return existingTx.get();
            }
        }

        // Regla 2: Instanciar la nueva transacción en estado PENDING por defecto
        Transaction transaction = Transaction.builder()
                .merchant(merchant)
                .customer(customer)
                .token(token)
                .amount(amount)
                .currency(currency)
                .idempotencyKey(idempotencyKey)
                .build();

        // Regla 3: Validaciones de Negocio (Simulación del motor de pagos)
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setStatusReason("MONTO_INVALIDO");
        } else if (token == null || !"ACTIVE".equalsIgnoreCase(token.getStatus())) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setStatusReason("TOKEN_O_TARJETA_INVALIDA");
        } else {
            // Transacción Aprobada
            transaction.setStatus(TransactionStatus.APPROVED);
            transaction.setStatusReason("APROBADO_CON_EXITO");
        }

        transaction.setUpdatedAt(LocalDateTime.now());

        // Regla 4: Guardar en el repositorio en memoria o MySQL
        return transactionRepository.save(transaction);
    }
}