package com.wanted.preonboarding.security.filter;

import com.wanted.preonboarding.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("doFilterInteral");

        putAuthToSecurityContext(request);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        log.info("check token");
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            log.error(NullPointerException.class.getSimpleName());
            return true;
        }

        if (!authorization.startsWith("Bearer ")) {
            log.error(MalformedJwtException.class.getSimpleName());
            return true;
        }
        //TODO: 로그아웃 기능 만들 시 로그아웃 필터 추가

        return false;
    }

    private void putAuthToSecurityContext(HttpServletRequest request) {
        try {
            setAuthToSecurityContext(jwtTokenProvider.getJwsBody(request));
        } catch (InsufficientAuthenticationException e1) {
            log.error(InsufficientAuthenticationException.class.getSimpleName());
        } catch (MalformedJwtException e1) {
            log.error(MalformedJwtException.class.getSimpleName());
        } catch (ExpiredJwtException e1) {
            log.error(ExpiredJwtException.class.getSimpleName());
        } catch (Exception e1) {
            log.error(Exception.class.getSimpleName());
        }
    }

    private void setAuthToSecurityContext(Map<String, Object> claims) {

        String username = (String) claims.get("username");
        String roles = (String) claims.get("roles");

        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_" + roles);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(username, null, authorityList);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("{}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

}
