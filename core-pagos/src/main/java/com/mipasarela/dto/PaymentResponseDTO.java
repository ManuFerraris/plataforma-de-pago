package com.mipasarela.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long transactionId;
    private String status;
    private String statusReason;
    private BigDecimal amount;
    private String currency;
    private String idempotencyKey;
    private LocalDateTime createdAt;
}