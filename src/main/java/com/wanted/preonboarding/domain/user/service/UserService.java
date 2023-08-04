package com.wanted.preonboarding.domain.user.service;


import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.domain.user.repository.UserRepository;
import com.wanted.preonboarding.web.dto.UserPostDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(UserPostDto userPostDto){
        User user = userPostDto.toEntity();
        String encodedPwd = passwordEncoder.encode(userPostDto.password());

        user.applyEncryptPassword(encodedPwd);

        userRepository.save(user);
        log.info("join complete");
    }
}
