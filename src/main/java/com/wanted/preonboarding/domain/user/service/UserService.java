package com.wanted.preonboarding.domain.user.service;


import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.domain.user.repository.UserRepository;
import com.wanted.preonboarding.global.exception.BusinessLoginException;
import com.wanted.preonboarding.global.exception.ExceptionCode;
import com.wanted.preonboarding.web.dto.JoinDto;
import jakarta.transaction.Transactional;
import java.util.Optional;
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

    public void signUp(JoinDto joinDto){
        verifyDuplicationEmail(joinDto.email());

        User user = joinDto.toEntity();
        String encodedPwd = passwordEncoder.encode(joinDto.password());

        user.applyEncryptPassword(encodedPwd);
        userRepository.save(user);
        log.info("join complete");
    }

    private void verifyDuplicationEmail(final String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            throw new BusinessLoginException(ExceptionCode.EXIST_EMAIL);
        }
    }
}
