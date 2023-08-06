package com.wanted.preonboarding;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class RefreshTokenTest extends ApiTest{

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUpEach(){
        userRepository.deleteAll();
    }

    @DisplayName("요청 시 리프레시 토큰이 발급된다.")
    @Test
    void post() throws Exception {
        //given
        String token = getToken();
        PostCreateDto postCreateDto = new PostCreateDto("title", "content");

        //@formatter:off
        given()
            .log().all()
            .header("Refresh", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("userId", user.getId())
            .body(postCreateDto)
            .when()
            .post("/posts")
            .then()
            .log().all()
            .header("Authorization", Matchers.notNullValue());

        //@formatter:on
    }

    private String getToken(){
        //given
        user = userService.signUp(new JoinDto("test@gmail.com", "1234456778"));
        LoginDto loginDto = new LoginDto("test@gmail.com", "1234456778");

        //@formatter:off
        ExtractableResponse<Response> response = given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginDto)
            .when()
            .post("/users/login")
            .then()
            .extract();
        //@formatter:on

        return response.header("Refresh");
    }
}
