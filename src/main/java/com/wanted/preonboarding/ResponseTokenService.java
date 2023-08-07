package com.wanted.preonboarding;


import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResponseTokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public void  delegateToken(HttpServletResponse response, User user) {
        String accessToken = jwtTokenProvider.delegateAccessToken(user);
        String refreshToken = jwtTokenProvider.delegateRefreshToken(user);

        refreshTokenRepository.save(user.getEmail(), refreshToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);
    }

    public void reissueToken( final HttpServletResponse response, final String email) {
        String token = getToken(email);
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Authorization", "Bearer " + token);
    }

    public Optional<String> checkToken(final String email) {
        return refreshTokenRepository.get(email);
    }

    private String getToken(final String email) {
        log.info("refresh token service");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return jwtTokenProvider.delegateAccessToken(optionalUser.get());
        }
        throw new BusinessLoginException(ExceptionCode.NOT_FOUND_USER);
    }
}
