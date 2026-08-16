package com.mipasarela.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyKey {
    private String idempotencyKey;
    private Merchant merchant;
    private String requestHash;
    private Integer responseCode;
    private String responseBody;
    private LocalDateTime createdAt;
}