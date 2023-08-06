package com.wanted.preonboarding;

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

    private final RefreshService refreshService;

    @Override
    public void commence(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final AuthenticationException authException
    ) throws IOException {
        log.info("AuthenticationEntryPoint");

        if (isDelegatingRefreshToken(request, response)) {
            return;
        }

        var exception = request.getAttribute("exception");
        ErrorResponser.sendError(response, (Exception) exception, HttpStatus.UNAUTHORIZED);
    }

    private boolean isDelegatingRefreshToken(
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        String email = (String) request.getAttribute("refresh");
        if (email != null) {
            String token = refreshService.getToken(email);
            response.setStatus(HttpStatus.OK.value());
            response.setHeader("Authorization", "Bearer " + token);
            return true;
        }
        return false;
    }
}
