package com.mipasarela.service;

import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Token;
import com.mipasarela.repository.TokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /*
     * Recibe datos sensibles de tarjeta, los valida, extrae únicamente
     * la información no sensible (últimos 4 dígitos) y genera un Token.
     */
    public Token tokenizeCard(
            Customer customer,
            String rawCardNumber,
            String cardHolderName,
            Integer expirationMonth,
            Integer expirationYear,
            String cvv) {
        // Limpieza de espacios y guiones
        String cleanCardNumber = rawCardNumber != null ? rawCardNumber.replaceAll("\\s+", "").replaceAll("-", "") : "";

        // Validación básica de número de tarjeta y CVV
        if (cleanCardNumber.length() < 13 || cleanCardNumber.length() > 19) {
            throw new IllegalArgumentException("Número de tarjeta inválido.");
        }
        if (cvv == null || cvv.length() < 3 || cvv.length() > 4) {
            throw new IllegalArgumentException("Código de seguridad (CVV) inválido.");
        }

        // Extracción segura: SOLO guardamos los últimos 4 dígitos
        String lastFour = cleanCardNumber.substring(cleanCardNumber.length() - 4);
        if (lastFour.length() != 4) {
            throw new IllegalArgumentException("No se pudieron extraer los últimos 4 dígitos de la tarjeta.");
        }
        ;

        // Identificación de la Franquicia (Visa, Mastercard, etc.)
        String brand = detectBrand(cleanCardNumber);

        // Generación de un token único (UUID)
        String generatedTokenValue = "tok_live_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // Construcción del objeto de Dominio (NUNCA guardamos cleanCardNumber ni cvv)
        Token token = Token.builder()
                .tokenValue(generatedTokenValue)
                .customer(customer)
                .lastFourDigits(lastFour)
                .cardHolderName(cardHolderName.toUpperCase())
                .expirationMonth(expirationMonth)
                .expirationYear(expirationYear)
                .brand(brand)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        // 7. Persistencia
        return tokenRepository.save(token);
    }

    private String detectBrand(String cardNumber) {
        if (cardNumber.startsWith("4"))
            return "VISA";
        if (cardNumber.matches("^5[1-5].*") || cardNumber.matches("^2[2-7].*"))
            return "MASTERCARD";
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37"))
            return "AMEX";
        return "UNKNOWN";
    }
}