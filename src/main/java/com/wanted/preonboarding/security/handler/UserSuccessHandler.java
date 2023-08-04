package com.wanted.preonboarding.security.handler;

import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.security.details.UserDetailsEntity;
import com.wanted.preonboarding.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Authentication authentication
    ) throws IOException, ServletException {

        UserDetailsEntity principal = (UserDetailsEntity) authentication.getPrincipal();
        User user = principal.user();
        jwtTokenProvider.addTokenInResponse(response, user);
    }
}
