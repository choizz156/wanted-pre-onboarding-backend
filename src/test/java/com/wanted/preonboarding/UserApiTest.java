package com.wanted.preonboarding;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class UserApiTest extends ApiTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("회원 가입 api 테스트")
    @Test
    void joinApi() throws Exception {
        //given
        JoinDto joinDto = new JoinDto("test@gmail.com", "1233456777");

        //expected
        //@formatter:off
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(joinDto)
        .when()
                .post("/users")
        .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("data", equalTo("회원 가입 완료"));
        //@formatter:on
    }

    @DisplayName("로그인을 하면 토큰이 발급된다")
    @Test
    void login() throws Exception {
        //given
        userService.signUp(new JoinDto("test@gmail.com", "1234456778"));
        LoginDto loginDto = new LoginDto("test@gmail.com", "1234456778");

        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginDto)
        .when()
                .post("/users/login")
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .header("Authorization", Matchers.notNullValue());
        //@formatter:on
    }

    private static Stream<Arguments> provideWrongInfo(){
        LoginDto wrongEmail = new LoginDto("test123@gmail.com", "12345678");
        LoginDto wrongPwd = new LoginDto("test@gmail.com", "123456782");

        return Stream.of(
            Arguments.of(wrongEmail),
            Arguments.of(wrongPwd)
        );
    }

    @DisplayName("로그인 실패 시 예외를 던진다.")
    @MethodSource("provideWrongInfo")
    @ParameterizedTest
    void auth(LoginDto loginDto) throws Exception {
        //given
        JoinDto joinDto = new JoinDto("test@gmail.com", "12345678");
        userService.signUp(joinDto);

        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginDto)
        .when()
                .post("/users/login")
        .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("status", equalTo(401))
                .body("msg", equalTo("자격 증명에 실패하였습니다."));
        //@formatter:on
    }
}
