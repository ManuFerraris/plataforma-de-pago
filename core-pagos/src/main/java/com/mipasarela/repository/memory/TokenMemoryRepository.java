package com.mipasarela.repository.memory;

import com.mipasarela.domain.Token;
import com.mipasarela.repository.TokenRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TokenMemoryRepository implements TokenRepository {

    private final Map<Long, Token> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Token save(Token token) {
        if (token.getTokenId() == null) {
            token.setTokenId(idSequence.getAndIncrement());
        }
        storage.put(token.getTokenId(), token);
        return token;
    }

    @Override
    public Optional<Token> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Token> findByTokenValue(String tokenValue) {
        if (tokenValue == null)
            return Optional.empty();

        return storage.values().stream()
                .filter(t -> tokenValue.equals(t.getTokenValue()))
                .findFirst();
    }
}