package com.wanted.preonboarding;

import static net.jodah.expiringmap.ExpiringMap.builder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenDao implements RefreshTokenRepository {

    private static final ExpiringMap<String, String> STORE =
        builder().expirationPolicy(ExpirationPolicy.CREATED)
        .expiration(500, TimeUnit.MINUTES)
        .maxSize(5000)
        .build();

    @Override
    public void save(final String email, final String refreshToken) {
        STORE.put(email, refreshToken);
    }

    @Override
    public Optional<String> get(final String email) {
        return Optional.of(STORE.get(email));
    }
}
