package com.mipasarela.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private Long tokenId;
    private Customer customer;
    private String lastFourDigits;
    private String cardHolderName;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String brand;
    private String status;
    private LocalDateTime createdAt;
}
