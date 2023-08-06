package com.wanted.preonboarding;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public String getToken(final String email) {
        log.info("refresh token service");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return jwtTokenProvider.delegateAccessToken(optionalUser.get());
        }
        throw new BusinessLoginException(ExceptionCode.NOT_FOUND_USER);
    }

    public Optional<String> checkToken(final String email) {
        return refreshTokenRepository.get(email);
    }
}
