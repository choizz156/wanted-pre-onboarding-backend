package com.wanted.preonboarding;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signUp(JoinDto joinDto){
        verifyDuplicationEmail(joinDto.email());

        User user = joinDto.toEntity();
        String encodedPwd = passwordEncoder.encode(joinDto.password());

        user.applyEncryptPassword(encodedPwd);
        userRepository.save(user);
        log.info("join complete");

        return user;
    }

    @Transactional(readOnly = true)
    public User findUser(final Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() -> new BusinessLoginException(ExceptionCode.NOT_FOUND_USER));
    }

    private void verifyDuplicationEmail(final String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            throw new BusinessLoginException(ExceptionCode.EXIST_EMAIL);
        }
    }
}
