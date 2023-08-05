package com.wanted.preonboarding;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    private final SecretKey secretKey;

    public String createAccessToken(
        Map<String, Object> claims,
        String subject,
        Date expiration,
        Key key
    ) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setExpiration(expiration)
            .setIssuedAt(Calendar.getInstance().getTime())
            .signWith(key)
            .compact();
    }

    public String extractJws(HttpServletRequest request) {
        return request.getHeader("Authorization").replace("Bearer ", "");
    }

    public Map<String, Object> getJwsBody(HttpServletRequest request) {
        String jws = extractJws(request);
        Key key = secretKey.getSecretKey();

        return getJws(jws, key).getBody();
    }

    public Jws<Claims> getJws(String jws, Key key) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jws);
    }

    public String delegateAccessToken(User user) {
        Map<String, Object> claims = new ConcurrentHashMap<>();

        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles().name());

        String subject = String.valueOf(user.getId());
        Date expiration = getExpiration(accessTokenExpirationMinutes);
        Key key = secretKey.getSecretKey();

        return createAccessToken(claims, subject, expiration, key);
    }

    private Date getExpiration(int tokenExpirationMinutes) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, tokenExpirationMinutes);
        return instance.getTime();
    }

    public void addTokenInResponse(HttpServletResponse response, User user) {
        String accessToken = delegateAccessToken(user);
        response.setHeader("Authorization", "Bearer " + accessToken);
    }
}
