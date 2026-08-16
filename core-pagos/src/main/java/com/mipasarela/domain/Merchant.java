package com.mipasarela.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    private Long merchantId;
    private String socialReason;
    private String fantasyName;
    private String cuit;
    private String categoryCode;
    private String direction;
    private String postalCode;
    private String email;
    private String phone;

    private String apiKey;
    private String secretKey;

    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
