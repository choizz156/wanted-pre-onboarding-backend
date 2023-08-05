package com.wanted.preonboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.wanted.preonboarding.JoinDto;
import com.wanted.preonboarding.BusinessLoginException;
import com.wanted.preonboarding.ExceptionCode;
import com.wanted.preonboarding.User;
import com.wanted.preonboarding.UserRepository;
import com.wanted.preonboarding.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @DisplayName("회원 가입을 할 수 있다.")
    @Test
    void join() throws Exception {
        //given
        JoinDto dto = new JoinDto("test@gmail.com", "testdfd11");

        //when
        userService.signUp(dto);

        //then
        User resultUser = userRepository.findAll().get(0);
        assertThat(resultUser.getEmail()).isEqualTo(dto.email());
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @DisplayName("회원 가입 시 이메일이 중복되는 경우 예외를 던진다.")
    @Test
    void duplication_email() throws Exception {
        //given
        JoinDto dto = new JoinDto("test@gmail.com", "testdfdf11");
        userService.signUp(dto);

        //when
        JoinDto dto2 = new JoinDto("test@gmail.com", "testdfd231");

        //then
        assertThatCode(() -> userService.signUp(dto2))
            .isInstanceOf(BusinessLoginException.class)
            .hasMessageContaining(ExceptionCode.EXIST_EMAIL.getMsg());
    }

    @DisplayName("비밀번호 암호화")
    @Test
    void pwd() throws Exception {
        //given
        String pwd = "12345asd";

        //when
        String result = passwordEncoder.encode(pwd);

        //then
        assertThat(passwordEncoder.matches(pwd, result)).isTrue();
    }
}
