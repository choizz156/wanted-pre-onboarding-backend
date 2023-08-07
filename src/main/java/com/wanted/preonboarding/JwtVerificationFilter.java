package com.wanted.preonboarding;

import static com.querydsl.core.util.StringUtils.isNullOrEmpty;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ResponseTokenService responseTokenService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String EXCEPTION_KEY = "exception";

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        log.info("check token");
        if (isRequestRefreshToken(request)) {
            return true;
        }

        String authorization = request.getHeader(AUTHORIZATION);

        if (checkNull(request, authorization)) {
            return true;
        }

        if (checkTokenForm(request, authorization)) {
            return true;
        }

        return false;
    }

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

    private boolean checkTokenForm(final HttpServletRequest request, final String authorization) {
        try {
            isMalformedToken(authorization);
        } catch (MalformedJwtException e) {
            log.info(MalformedJwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
            return true;
        }
        return false;
    }

    private Map<String, Object> verifyJws(final HttpServletRequest request, final String jws) {
        Map<String, Object> claims = new HashMap<>();
        try {
            claims = jwtTokenProvider.getJwsBody(jws).getBody();
        } catch (SignatureException e) {
            log.info(SignatureException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
        } catch (ExpiredJwtException e) {
            log.info(ExpiredJwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
        } catch (JwtException e) {
            log.info(JwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
        }
        return claims;
    }

    private boolean checkNull(final HttpServletRequest request, final String authorization) {
        try {
            isNullToken(authorization);
        } catch (NullPointerException e) {
            log.warn(NullPointerException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
            return true;
        }
        return false;
    }

    private void isMalformedToken(final String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            throw new MalformedJwtException("Malformed Token");
        }
    }

    private void isNullToken(final String authorization) {
        if (authorization == null) {
            throw new NullPointerException("No Token");
        }
    }

    private void putAuthToSecurityContext(HttpServletRequest request) {
        String jws = request.getHeader(AUTHORIZATION).replace("Bearer ", "");
        setAuthToSecurityContext(verifyJws(request, jws));
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

    private boolean isRequestRefreshToken(final HttpServletRequest request) {
        String refreshJws = request.getHeader("Refresh");
        return !isNullOrEmpty(refreshJws) && isDelegatePossible(request, refreshJws);
    }

    private boolean isDelegatePossible(final HttpServletRequest request, final String refreshJws) {
        String subject = (String) verifyJws(request, refreshJws).get("sub");
        Optional<String> refreshToken = responseTokenService.checkToken(subject);
        if (refreshToken.isPresent()) {
            request.setAttribute("refresh", subject);
            return true;
        }
        return false;
    }
}
