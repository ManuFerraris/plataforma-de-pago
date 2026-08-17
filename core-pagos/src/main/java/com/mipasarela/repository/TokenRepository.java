package com.mipasarela.repository;

import com.mipasarela.domain.Token;

import java.util.Optional;

public interface TokenRepository {
    Token save(Token token);

    Optional<Token> findById(Long id);

    Optional<Token> findByTokenValue(String tokenValue); // Para buscar por el hash/string del token
}