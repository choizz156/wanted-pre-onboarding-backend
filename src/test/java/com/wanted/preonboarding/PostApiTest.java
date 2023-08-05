package com.wanted.preonboarding;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class PostApiTest extends ApiTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    private User user;

    @BeforeEach
    void setUpEach(){
        postRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(new User("test@gmail.com", "12345678"));
    }

    @DisplayName("posting 생성")
    @Test
    void post() throws Exception {
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
                .body("createdAt", notNullValue())
                .body("data.title",equalTo("title"))
                .body("data.content",equalTo("content"));
        //@formatter:on
    }

    @DisplayName("posting 생성 시 유저를 찾지 못하면 예외를 던진다.")
    @Test
    void post_exception() throws Exception {
        //given
        PostCreateDto postCreateDto = new PostCreateDto("title", "content");

        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("userId", 11L)
                .body(postCreateDto)
        .when()
                .post("/posts")
        .then()
                .log().all()
                .body("createdAt", notNullValue())
                .body("status", equalTo(400))
                .body("msg", equalTo("찾을 수 없는 회원입니다."));

        //@formatter:on
    }

    @DisplayName("posting 내용은 작성자만이 수정할 수 있다.")
    @Test
    void edit() throws Exception {
        //given
        Post posting = postService.posting(user.getId(), new PostCreateDto("title", "content"));
        PostEditDto postEditDto = new PostEditDto("title1", "content1");

        //expected
        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("postId", posting.getId())
                .queryParam("userId", user.getId())
                .body(postEditDto)
        .when()
                .patch("/posts/{postId}")
        .then()
                .log().all()
                .body("createdAt", notNullValue())
                .body("data.title", equalTo("title1"))
                .body("data.content", equalTo("content1"));
        //@formatter:on
    }

    @DisplayName("posting의 작성자가 아닌 유저가 수정 시도 시 예외를 던진다.")
    @Test
    void edit_exception() throws Exception {
        //given
        postService.posting(user.getId(), new PostCreateDto("title", "content"));
        PostEditDto postEditDto = new PostEditDto("title1", "content1");

        //expected
        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("postId", 1L)
                .queryParam("userId", 11L)
                .body(postEditDto)
        .when()
                .patch("/posts/{postId}")
        .then()
                .log().all()
                .body("createdAt", notNullValue())
                .body("status", equalTo(400))
                .body("msg", equalTo(ExceptionCode.NOT_MATCHING_OWNER.getMsg()));
        //@formatter:on
    }

    @DisplayName("posting 수정 시 posting이 존재하지 않으면 예외를 던진다.")
    @Test
    void edit_exception2() throws Exception {
        //given
        postService.posting(user.getId(), new PostCreateDto("title", "content"));
        PostEditDto postEditDto = new PostEditDto("title1", "content1");

        //expected
        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("postId", 11L)
                .queryParam("userId", user.getId())
                .body(postEditDto)
        .when()
                .patch("/posts/{postId}")
        .then()
                .log().all()
                .body("createdAt", notNullValue())
                .body("status", equalTo(400))
                .body("msg", equalTo(ExceptionCode.NOT_FOUND_POST.getMsg()));
        //@formatter:on
    }
}
