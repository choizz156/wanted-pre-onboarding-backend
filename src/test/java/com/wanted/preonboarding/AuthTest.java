package com.wanted.preonboarding;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("authorization test")
class AuthTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        user = userService.signUp(new JoinDto("test@gmail.com", "1234456778"));
    }

    @DisplayName("로그인하지 않으면 포스팅을 할 수 없다.")
    @Test
    void post_auth() throws Exception {
        //given
        PostCreateDto postCreateDto = new PostCreateDto("title", "content");

        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("userId", user.getId())
                .body(postCreateDto)
        .when()
                .post("/posts")
        .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("time", notNullValue())
                .body("data.status", equalTo(HttpStatus.UNAUTHORIZED.value()))
                .body("data.msg", equalTo("No Token"));
        //@formatter:on
    }

    @DisplayName("로그인하지 않으면 포스팅을 수정할 수 없다.")
    @Test
    void edit() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));
        PostEditDto postEditDto = new PostEditDto("title1", "content1");

        //expected
        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("postId", post.getId())
                .queryParam("userId", user.getId())
                .body(postEditDto)
        .when()
                .patch("/posts/{postId}")
        .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("time", notNullValue())
                .body("data.status", equalTo(HttpStatus.UNAUTHORIZED.value()))
                .body("data.msg", equalTo("No Token"));
        //@formatter:on
    }
    @DisplayName("로그인하지 않으면 포스팅을 삭제할 수 없다.")
    @Test
    void delete() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));

        //@formatter:off
        given()
                .log().all()
        .when()
                .delete("/posts/{postId}", post.getId())
        .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("time", notNullValue())
                .body("data.status", equalTo(HttpStatus.UNAUTHORIZED.value()))
                .body("data.msg", equalTo("No Token"));
        //@formatter:on
    }
}
