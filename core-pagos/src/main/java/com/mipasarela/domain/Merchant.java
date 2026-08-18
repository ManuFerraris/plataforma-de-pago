package com.mipasarela.domain;

import jakarta.persistence.*;

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
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "razon_social", nullable = false, length = 100)
    private String socialReason;

    @Column(name = "nombre_fantasia", length = 100)
    private String fantasyName;

    @Column(name = "cuit", nullable = false, unique = true, length = 20)
    private String cuit;

    @Column(name = "codigo_categoria", length = 10)
    private String categoryCode;

    @Column(name = "direccion", length = 200)
    private String address;

    @Column(name = "codigo_postal", length = 20)
    private String postalCode;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefono", length = 50)
    private String phone;

    @Column(name = "api_key", unique = true, length = 100)
    private String apiKey;

    @Column(name = "secret_key", length = 100)
    private String secretKey;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
