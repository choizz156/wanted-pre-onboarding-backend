package com.wanted.preonboarding.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.wanted.preonboarding.web.dto.JoinDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ValidationTest extends ApiTest {

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
                .body("status", equalTo(400))
                .body("createdAt", notNullValue())
                .body("customFieldErrors[0].field", equalTo("email"))
                .body("customFieldErrors[0].rejectedValue", equalTo("testgmail.com"))
                .body("customFieldErrors[0].reason", equalTo("이메일 형식이어야 합니다."));
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
                .body("status", equalTo(400))
                .body("createdAt", notNullValue())
                .body("customFieldErrors[0].field", equalTo("password"))
                .body("customFieldErrors[0].rejectedValue", equalTo("sdf"))
                .body("customFieldErrors[0].reason", equalTo("비밀번호는 8자리 이상이어야 합니다."));
        //@formatter:on
    }
}
