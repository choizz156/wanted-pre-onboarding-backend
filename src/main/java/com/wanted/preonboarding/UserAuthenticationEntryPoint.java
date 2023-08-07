package com.wanted.preonboarding;

import static com.wanted.preonboarding.AuthErrorResponse.sendError;

import com.querydsl.core.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseTokenService responseTokenService;

    @Override
    public void commence(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final AuthenticationException authException
    ) throws IOException {
        log.info("AuthenticationEntryPoint");
        String email = (String) request.getAttribute("refresh");
        if (!StringUtils.isNullOrEmpty(email)) {
            responseTokenService.reissueToken(response, email);
        }

        var exception = request.getAttribute("exception");
        sendError(response, (Exception) exception, HttpStatus.UNAUTHORIZED);
    }
}
