package com.wanted.preonboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String POST_URL = "/posts/**";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(new CorsConfig().corsFilter()))
            .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.accessDeniedHandler(new UserAccessDeniedHandler());
                    exceptionHandling.authenticationEntryPoint(new UserAuthenticationEntryPoint());
                }
            )
            .apply(new CustomFilterConfig(jwtTokenProvider));

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.POST, POST_URL).hasRole("USER");
            auth.requestMatchers(HttpMethod.DELETE, POST_URL).hasRole("USER");
            auth.requestMatchers(HttpMethod.PATCH, POST_URL).hasRole("USER");
            auth.anyRequest().permitAll();
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
