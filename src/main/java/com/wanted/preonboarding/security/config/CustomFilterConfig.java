package com.wanted.preonboarding.security.config;

import com.wanted.preonboarding.security.filter.JwtLoginFilter;
import com.wanted.preonboarding.security.filter.JwtVerificationFilter;
import com.wanted.preonboarding.security.handler.UserFailureHandler;
import com.wanted.preonboarding.security.handler.UserSuccessHandler;
import com.wanted.preonboarding.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomFilterConfig extends AbstractHttpConfigurer<CustomFilterConfig, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(final HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager =
            builder.getSharedObject(AuthenticationManager.class);

        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager);
        jwtLoginFilter.setFilterProcessesUrl("/users/login");

        jwtLoginFilter.setAuthenticationFailureHandler(new UserFailureHandler());
        jwtLoginFilter.setAuthenticationSuccessHandler(new UserSuccessHandler(jwtTokenProvider));

        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenProvider);

        builder.addFilter(jwtLoginFilter)
            .addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);
    }
}