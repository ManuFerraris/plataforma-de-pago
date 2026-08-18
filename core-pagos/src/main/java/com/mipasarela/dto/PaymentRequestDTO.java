package com.mipasarela.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    
    @NotNull(message = "El ID del comercio es obligatorio")
    private Long merchantId;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;
    
    @NotBlank(message = "El token de pago no puede estar vacío")
    private String tokenValue; 
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;
    
    @NotBlank(message = "La moneda es obligatoria")
    private String currency;
}