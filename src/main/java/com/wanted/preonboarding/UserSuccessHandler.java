package com.wanted.preonboarding;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ResponseTokenService responseTokenService;

    @Override
    public void onAuthenticationSuccess(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Authentication authentication
    ) {

        UserDetailsEntity principal = (UserDetailsEntity) authentication.getPrincipal();
        User user = principal.user();
        responseTokenService.delegateToken(response,user);
    }
}
