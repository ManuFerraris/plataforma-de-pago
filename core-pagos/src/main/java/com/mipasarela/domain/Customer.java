package com.mipasarela.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    private Long customerId;
    private String dniNumber;
    private String customerCategory;
    private String name;
    private String lastName;
    private String email;
}
