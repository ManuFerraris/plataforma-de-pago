package com.mipasarela.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Long merchantId;
    private Long customerId;
    private String tokenValue; // El hash del token, ej. "tok_live_123"
    private BigDecimal amount;
    private String currency;
}