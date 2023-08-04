package com.wanted.preonboarding.global.security.filter;

import com.wanted.preonboarding.global.security.dto.LoginDto;
import com.wanted.preonboarding.global.security.util.Mapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response
    ) throws AuthenticationException {

        log.info("trying login");
        LoginDto loginDto = getLoginDto(request);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password());

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain chain,
        final Authentication authResult
    ) throws IOException, ServletException {

        log.info("login success");
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private LoginDto getLoginDto(HttpServletRequest request) {
        try {
            return Mapper.getInstance().readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }
}
