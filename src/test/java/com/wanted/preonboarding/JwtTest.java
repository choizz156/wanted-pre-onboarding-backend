package com.wanted.preonboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNoException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("jwt token 테스트")
class JwtTest {

    private String baseKey;
    private String encodedKey;
    private Key key;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void beforeEach() {
        baseKey = "12312312312313123sdfsgsddfdfdfdfdffsdf";
        key = getKey();
        jwtTokenProvider = new JwtTokenProvider(new SecretKey());
    }

    @DisplayName("액세스 토큰과 리프레시 토큰이 발급이 발급된다.")
    @Test
    void acccesToken() throws Exception {
        //given when
        String accessToken = getAccessToken(Calendar.MINUTE, 10, key);
        String refreshToken = getRefreshToken(Calendar.MINUTE, 10, key);
        //then
        assertThat(accessToken).isNotNull();
        assertThat(refreshToken).isNotNull();
    }

    @DisplayName("jws 검증할 경우 예외를 던지 않는다.")
    @Test
    void jwsTest() throws Exception {
        //given
        String accessToken = getAccessToken(Calendar.MINUTE, 10, key);

        //expected
        assertThatNoException().isThrownBy(() -> jwtTokenProvider.getJws(accessToken, key));
    }

    @DisplayName("access token의 기한이 지나면 예외를 던진다.")
    @Test
    void expirationTest() throws Exception {
        //given
        String accessToken = getAccessToken(Calendar.SECOND, 1, key);

        TimeUnit.SECONDS.sleep(2);

        //expected
        assertThatCode(() ->
            jwtTokenProvider.getJws(accessToken, key)
        ).isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("refresh token의 기한이 지나면 예외를 던진다.")
    @Test
    void expirationTest2() throws Exception {
        //given
        String refreshToken = getRefreshToken(Calendar.SECOND, 1, key);

        TimeUnit.SECONDS.sleep(2);

        //expected
        assertThatCode(() ->
            jwtTokenProvider.getJws(refreshToken, key)
        ).isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("토큰이 잘못된 형식이라면 예외를 던집니다.")
    @Test
    void signature() throws Exception {
        //given
        String refreshToken = getRefreshToken(Calendar.SECOND, 1, key) + 1;

        //expected
        assertThatCode(() ->
            jwtTokenProvider.getJws(refreshToken, key)
        ).isInstanceOf(SignatureException.class);
    }

    @DisplayName("토큰이 잘못된 형식이라면 예외를 던집니다.")
    @Test
    void malformed() throws Exception {
        //given
        String refreshToken = 1 + getRefreshToken(Calendar.SECOND, 1, key);

        //expected
        assertThatCode(() ->
            jwtTokenProvider.getJws(refreshToken, key)
        ).isInstanceOf(MalformedJwtException.class);
    }

    private String getAccessToken(int timeUnit, int timeAmount, final Key key) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", 1);
        claims.put("roles", List.of("USER"));

        String subject = "test access token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(timeUnit, timeAmount);
        Date expiration = calendar.getTime();

        return jwtTokenProvider.createAccessToken(claims, subject, expiration, key);
    }

    private String getRefreshToken(int timeUnit, int timeAmount, final Key key) {

        String subject = "test refresh token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(timeUnit, timeAmount);
        Date expiration = calendar.getTime();

        return jwtTokenProvider.createRefreshToken(subject, expiration, key);
    }

    private Key getKey() {
        encodedKey = Encoders.BASE64.encode(baseKey.getBytes(StandardCharsets.UTF_8));
        byte[] decode = Decoders.BASE64.decode(encodedKey);
        return Keys.hmacShaKeyFor(decode);
    }
}
