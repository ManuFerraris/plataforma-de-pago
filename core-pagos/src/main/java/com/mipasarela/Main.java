package com.mipasarela;

import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Merchant;
import com.mipasarela.domain.Token;
import com.mipasarela.domain.Transaction;
import com.mipasarela.repository.CustomerRepository;
import com.mipasarela.repository.MerchantRepository;
import com.mipasarela.repository.TokenRepository;
import com.mipasarela.repository.TransactionRepository;
import com.mipasarela.repository.mysql.CustomerMySQLRepository;
import com.mipasarela.repository.mysql.MerchantMySQLRepository;
import com.mipasarela.repository.mysql.TokenMySQLRepository;
import com.mipasarela.repository.mysql.TransactionMySQLRepository;
import com.mipasarela.service.TokenService;
import com.mipasarela.service.TransactionService;

import java.math.BigDecimal;
import java.util.UUID;

public class Main {
        public static void main(String[] args) {
                System.out.println("==========================================================");
                System.out.println("   Iniciando prueba END-TO-END con persistencia en MYSQL");
                System.out.println("==========================================================");

                MerchantRepository merchantRepo = new MerchantMySQLRepository();
                CustomerRepository customerRepo = new CustomerMySQLRepository();
                TokenRepository tokenRepo = new TokenMySQLRepository();
                TransactionRepository txRepo = new TransactionMySQLRepository();

                // Instanciamos los servicios inyectando los repositorios MySQL
                TokenService tokenService = new TokenService(tokenRepo);
                TransactionService txService = new TransactionService(txRepo);

                // Crear y Persistir Comercio
                String uniqueCuit = "30-71" + (int) (Math.random() * 899999 + 100000) + "-8";
                Merchant merchant = Merchant.builder()
                                .socialReason("Comercio Rosario Digital S.A.")
                                .fantasyName("Tienda Rosario")
                                .cuit(uniqueCuit)
                                .email("contacto@tiendarosario.com")
                                .apiKey("pk_live_" + UUID.randomUUID().toString().substring(0, 8))
                                .secretKey("sk_live_" + UUID.randomUUID().toString().substring(0, 16))
                                .build();

                merchant = merchantRepo.save(merchant);
                System.out.println(" [OK] Comercio Guardado en MySQL [ID: " + merchant.getMerchantId() + "]");

                // 4. Crear y Persistir Cliente
                String uniqueDni = String.valueOf((int) (Math.random() * 89999999 + 10000000));
                Customer customer = Customer.builder()
                                .dniNumber(uniqueDni)
                                .customerCategory("CONSUMIDOR_FINAL")
                                .name("Manuel")
                                .lastName("González")
                                .email("manuel_" + uniqueDni + "@email.com")
                                .build();

                customer = customerRepo.save(customer);
                System.out.println("[OK] Cliente Guardado en MySQL [ID: " + customer.getCustomerId() + "]");

                // Tokenizar Tarjeta de Crédito
                System.out.println("\n--- Ejecutando Tokenización de Tarjeta ---");
                Token token = tokenService.tokenizeCard(
                                customer,
                                "4548 1234 5678 9900",
                                "Manuel González",
                                12,
                                2029,
                                "888");
                System.out.println("[OK] Token Guardado en MySQL [ID: " + token.getTokenId() + " | Token: "
                                + token.getTokenValue() + "]");

                // 6. Procesar Pago y Persistir Transacción
                System.out.println("\n--- Procesando Transacción Financiera ---");
                String idempotencyKey = "KEY-ORDER-" + System.currentTimeMillis();
                Transaction tx1 = txService.processPayment(
                                merchant,
                                customer,
                                token,
                                new BigDecimal("12500.00"),
                                "ARS",
                                idempotencyKey);

                System.out.println("[OK] Transacción Guardada en MySQL [ID: " + tx1.getTransactionId() + "]");
                System.out.println("   Estado: " + tx1.getStatus() + " | Detalle: " + tx1.getStatusReason());
                System.out.println("   Monto: " + tx1.getCurrency() + " " + tx1.getAmount());

                // 7. Simular Reintento de Red con la misma IdempotencyKey contra MySQL
                System.out.println("\n--- Simulando Reintento HTTP con Misma Clave de Idempotencia ---");
                Transaction tx2 = txService.processPayment(
                                merchant,
                                customer,
                                token,
                                new BigDecimal("12500.00"),
                                "ARS",
                                idempotencyKey);

                System.out.println(
                                "[Busqueda] Respuesta devuelta por MySQL para reintento: [ID: "
                                                + tx2.getTransactionId() + "]");
                System.out.println("   ¿Coinciden los IDs de ambas transacciones?: "
                                + (tx1.getTransactionId().equals(tx2.getTransactionId())
                                                ? "Si (Idempotencia verificada en MySQL)"
                                                : "No (Error)"));
        }
}