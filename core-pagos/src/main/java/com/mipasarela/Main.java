package com.mipasarela;

import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Merchant;
import com.mipasarela.domain.Token;
import com.mipasarela.domain.Transaction;
import com.mipasarela.repository.TokenRepository;
import com.mipasarela.repository.TransactionRepository;
import com.mipasarela.repository.memory.TokenMemoryRepository;
import com.mipasarela.repository.memory.TransactionMemoryRepository;
import com.mipasarela.service.TokenService;
import com.mipasarela.service.TransactionService;

import java.math.BigDecimal;

public class Main {
        public static void main(String[] args) {
                // 1. Inicialización de Repositorios en Memoria
                TransactionRepository txRepository = new TransactionMemoryRepository();
                TokenRepository tokenRepository = new TokenMemoryRepository();

                // Inicialización de Servicios
                TransactionService txService = new TransactionService(txRepository);
                TokenService tokenService = new TokenService(tokenRepository);

                // Creación de Entidades Base
                Merchant merchant = Merchant.builder().merchantId(1L).socialReason("E-Commerce Rosario S.R.L.").build();
                Customer customer = Customer.builder().customerId(10L).name("Manuel").lastName("González").build();

                // Tokenización de Tarjeta (Simulando lo que haría el cliente)
                System.out.println("--- Paso 1: Tokenizando Tarjeta de Crédito ---");
                Token tokenGenerado = tokenService.tokenizeCard(
                                customer,
                                "4548 1234 5678 9012", // Tarjeta Visa simulada
                                "Manuel Sanchez",
                                10,
                                2028,
                                "888");

                System.out.println("Token Generado: " + tokenGenerado.getTokenValue());
                System.out.println("Marca: " + tokenGenerado.getBrand() + " | Ultimos 4 dígitos: "
                                + tokenGenerado.getLastFourDigits());

                // Procesamiento de Pago usando el Token recién creado
                System.out.println("\n--- Paso 2: Procesando Pago con el Token ---");
                Transaction tx = txService.processPayment(
                                merchant,
                                customer,
                                tokenGenerado,
                                new BigDecimal("4500.50"),
                                "ARS",
                                "KEY-ORDER-99");

                System.out.println("Transacción ID: " + tx.getTransactionId());
                System.out.println("Estado: " + tx.getStatus() + " | Detalle: " + tx.getStatusReason());
                System.out.println("Monto: " + tx.getCurrency() + " " + tx.getAmount());
        }
}