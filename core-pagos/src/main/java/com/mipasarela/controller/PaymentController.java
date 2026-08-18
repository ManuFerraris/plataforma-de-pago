package com.mipasarela.controller;

import jakarta.validation.Valid;

import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Merchant;
import com.mipasarela.domain.Token;
import com.mipasarela.domain.Transaction;
import com.mipasarela.dto.PaymentRequestDTO;
import com.mipasarela.dto.PaymentResponseDTO;
import com.mipasarela.repository.CustomerRepository;
import com.mipasarela.repository.MerchantRepository;
import com.mipasarela.repository.TokenRepository;
import com.mipasarela.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final TransactionService transactionService;
    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;
    private final TokenRepository tokenRepository;

    // Inyección de dependencias por constructor (Estándar recomendado en Spring)
    public PaymentController(
            TransactionService transactionService,
            MerchantRepository merchantRepository,
            CustomerRepository customerRepository,
            TokenRepository tokenRepository) {
        this.transactionService = transactionService;
        this.merchantRepository = merchantRepository;
        this.customerRepository = customerRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PaymentRequestDTO request) {
        // 1. Validar existencia de las entidades requeridas
        Optional<Merchant> merchantOpt = merchantRepository.findById(request.getMerchantId());
        if (merchantOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Token> tokenOpt = tokenRepository.findByTokenValue(request.getTokenValue());
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 2. Invocar el servicio de negocio existente
        Transaction tx = transactionService.processPayment(
                merchantOpt.get(),
                customerOpt.get(),
                tokenOpt.get(),
                request.getAmount(),
                request.getCurrency(),
                idempotencyKey);

        // 3. Mapear el resultado de Dominio a DTO de Respuesta
        PaymentResponseDTO responseDTO = PaymentResponseDTO.builder()
                .transactionId(tx.getTransactionId())
                .status(tx.getStatus().name())
                .statusReason(tx.getStatusReason())
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .idempotencyKey(tx.getIdempotencyKey())
                .createdAt(tx.getCreatedAt())
                .build();

        // 4. Devolver respuesta HTTP 201 CREATED con el cuerpo JSON
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}