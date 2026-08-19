package com.mipasarela.service;

import com.mipasarela.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j // Lo uso para crear una variable estatica automaticamente con lombok.
public class WebhookService {

    @Async
    public void notifyMerchant(Transaction transaction) {
        // Reemplazo mi sistem.out.println por un log.info para que quede registrado en el log de la aplicación.
        log.info("[\uD83D\uDD14 WEBHOOK] Preparando notificación para el comercio ID: {}", transaction.getMerchant().getMerchantId());
        
        try {
            // Simulo el retraso de red al intentar comunicarnos con el servidor del E-commerce
            Thread.sleep(3000); 
            
            // Aquí en la vida real usaríamos un HttpClient para hacer un POST a la URL del comercio
            // Ejemplo: POST https://tienda.com/api/webhooks/pagos
            
            String payload = String.format(
                "{\"transactionId\": %d, \"status\": \"%s\", \"amount\": %s}",
                transaction.getTransactionId(),
                transaction.getStatus().name(),
                transaction.getAmount().toString()
            );
            
            log.info("[\uD83D\uDCE4 WEBHOOK] Petición enviada con éxito al E-commerce: {}", payload);            
        } catch (InterruptedException e) {
            log.error("[\u274C WEBHOOK] Fallo al notificar al comercio.", e);
            Thread.currentThread().interrupt();
        }
    }
}