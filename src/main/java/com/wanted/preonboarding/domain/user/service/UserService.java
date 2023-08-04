package com.wanted.preonboarding.domain.user.service;


import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.domain.user.repository.UserRepository;
import com.wanted.preonboarding.web.dto.UserPostDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public void signUp(UserPostDto userPostDto){
        User entity = userPostDto.toEntity();
        userRepository.save(entity);
        log.info("회원 가입 완료");
    }
}
