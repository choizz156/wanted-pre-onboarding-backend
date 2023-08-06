package com.wanted.preonboarding;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("user api 테스트, 로그인 포함")
class UserApiTest extends ApiTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("회원 가입")
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
                .body("data", equalTo(joinDto.email()));
        //@formatter:on
    }

    @DisplayName("회원 가입 시 이메일이 중복되는 경우 예외를 던진다.")
    @Test
    void duplication_email() throws Exception {
        //given
        JoinDto dto = new JoinDto("test@gmail.com", "testdfdf11");
        userService.signUp(dto);

        //when
        JoinDto dto2 = new JoinDto("test@gmail.com", "testdfd231");

        //expected
        //@formatter:off
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto2)
        .when()
                .post("/users")
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("data.status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("data.msg", equalTo(ExceptionCode.EXIST_EMAIL.getMsg()));
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

    private static Stream<Arguments> provideWrongInfo() {
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
                .body("data.status", equalTo(401))
                .body("data.msg", is(in(new String[]{"자격 증명에 실패하였습니다.","Bad credentials"})));
        //@formatter:on
    }

    @DisplayName("이메일에 @가 포함되어 있지 않으면 예외를 던진다.")
    @Test
    void email() throws Exception {
        //given
        JoinDto dto = new JoinDto("testgmail.com", "sdfsgsdfe");

        //@formatter:off
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto)
        .when()
                .post("/users")
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("data.status", equalTo(400))
                .body("time", notNullValue())
                .body("data.customFieldErrors[0].field", equalTo("email"))
                .body("data.customFieldErrors[0].rejectedValue", equalTo("testgmail.com"))
                .body("data.customFieldErrors[0].reason", equalTo("@가 포함되어야 합니다."));
        //@formatter:on
    }

    @DisplayName("비밀번호가 8자리 이하라면 예외를 던진다.")
    @Test
    void password() throws Exception {
        //given
        JoinDto dto = new JoinDto("test@gmail.com", "sdf");

        //@formatter:off
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto)
        .when()
                .post("/users")
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("data.status", equalTo(400))
                .body("time", notNullValue())
                .body("data.customFieldErrors[0].field", equalTo("password"))
                .body("data.customFieldErrors[0].rejectedValue", equalTo("sdf"))
                .body("data.customFieldErrors[0].reason", equalTo("비밀번호는 8자리 이상이어야 합니다."));
        //@formatter:on
    }

}
