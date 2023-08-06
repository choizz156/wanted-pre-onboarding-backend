package com.wanted.preonboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomFilterConfig extends AbstractHttpConfigurer<CustomFilterConfig, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshService refreshService;

    @Override
    public void configure(final HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager =
            builder.getSharedObject(AuthenticationManager.class);

        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager);
        jwtLoginFilter.setFilterProcessesUrl("/users/login");

        jwtLoginFilter.setAuthenticationFailureHandler(new UserFailureHandler());
        jwtLoginFilter.setAuthenticationSuccessHandler(
            new UserSuccessHandler(jwtTokenProvider, refreshTokenRepository)
        );
        JwtVerificationFilter jwtVerificationFilter =
            new JwtVerificationFilter(jwtTokenProvider, refreshService);

        builder.addFilter(jwtLoginFilter)
            .addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);
    }
}
