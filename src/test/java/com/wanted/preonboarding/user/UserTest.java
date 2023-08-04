package com.wanted.preonboarding.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.domain.user.repository.UserRepository;
import com.wanted.preonboarding.domain.user.service.UserService;
import com.wanted.preonboarding.web.dto.UserPostDto;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원 가입을 할 수 있다.")
    @Test
    void 회원가입() throws Exception {
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
}
