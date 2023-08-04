package com.wanted.preonboarding.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wanted.preonboarding.security.jwt.JwtTokenProvider;
import com.wanted.preonboarding.security.jwt.SecretKey;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
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

class JwtTest {

    private String baseKey;
    private String encodedKey;
    private Key key;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void beforeEach() {
        baseKey = "12312312312313123sdfsgsddfdfdfdfdffsdf";
        encodedKey = Encoders.BASE64.encode(baseKey.getBytes(StandardCharsets.UTF_8));
        byte[] decode = Decoders.BASE64.decode(encodedKey);
        key = Keys.hmacShaKeyFor(decode);
        jwtTokenProvider = new JwtTokenProvider(new SecretKey());
    }

    @DisplayName("액세스 토큰 발급이 발급된다.")
    @Test
    void acccesToken() throws Exception {
        //given
        String accessToken = getAccessToken(Calendar.MINUTE, 10, key);

        //when then
        assertThat(accessToken).isNotNull();
    }

    @DisplayName("jws 검증할 경우 예외를 던지 않는다.")
    @Test
    void jwsTest() throws Exception {
        //given
        String accessToken = getAccessToken(Calendar.MINUTE, 10, key);

        //when then
        assertThatNoException().isThrownBy(() -> jwtTokenProvider.getJws(accessToken, key));
    }

    @DisplayName("access token의 기한이 지나면 예외를 던진다.")
    @Test
    void expirationTest() throws Exception {
        //given
        String accessToken = getAccessToken(Calendar.SECOND, 1, key);

        //when
        TimeUnit.SECONDS.sleep(2);

        //then
        assertThatCode(() ->
            jwtTokenProvider.getJws(accessToken, key)
        ).isInstanceOf(ExpiredJwtException.class);
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
}
