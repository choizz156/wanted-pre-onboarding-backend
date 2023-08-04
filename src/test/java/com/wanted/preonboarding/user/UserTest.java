package com.wanted.preonboarding.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.domain.user.repository.UserRepository;
import com.wanted.preonboarding.domain.user.service.UserService;
import com.wanted.preonboarding.global.exception.BusinessLoginException;
import com.wanted.preonboarding.global.exception.ExceptionCode;
import com.wanted.preonboarding.web.dto.UserPostDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class UserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void setUp() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원 가입을 할 수 있다.")
    @Test
    void join() throws Exception {
        //given
        UserPostDto dto = new UserPostDto("test@gmail.com", "testdfd11");

        //when
        userService.signUp(dto);

        //then
        Optional<User> userOptional = userRepository.findById(1L);
        List<User> list = userRepository.findAll();

        assertThat(userOptional.get().getEmail()).isEqualTo(dto.email());
        assertThat(list).hasSize(1);
    }

    @DisplayName("회원 가입 시 이메일이 중복되는 경우 예외를 던진다.")
    @Test
    void duplication_email() throws Exception {
        //given
        UserPostDto dto = new UserPostDto("test@gmail.com", "testdfdf11");
        userService.signUp(dto);

        //when
        UserPostDto dto2 = new UserPostDto("test@gmail.com", "testdfd231");

        //then
        assertThatCode(() -> userService.signUp(dto2))
            .isInstanceOf(BusinessLoginException.class)
            .hasMessageContaining(ExceptionCode.EXIST_EMAIL.getMsg());
    }
}
