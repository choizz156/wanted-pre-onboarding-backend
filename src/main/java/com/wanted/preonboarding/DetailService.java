package com.wanted.preonboarding;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<User> userEntity = userRepository.findByEmail(username);

        User user = userEntity.orElseThrow(() -> new UsernameNotFoundException("Not Found User"));
        log.info("authentication success");

        return UserDetailsEntity.builder().user(user).build();
    }
}
