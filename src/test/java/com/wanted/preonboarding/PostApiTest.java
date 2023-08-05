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

class PostApiTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    private User user;

    @BeforeEach
    void setUpEach() {
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
                .body("time", notNullValue())
                .body("data.title",equalTo("title"))
                .body("data.content",equalTo("content"))
                .body("data.userId", equalTo(user.getId().intValue()))
                .body("data.createdAt", notNullValue())
                .body("data.modifiedAt", notNullValue());
        //@formatter:on
    }


    @DisplayName("posting 내용은 작성자만이 수정할 수 있다.")
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
                .body("time", notNullValue())
                .body("data.title", equalTo("title1"))
                .body("data.content", equalTo("content1"))
                .body("data.userId", equalTo(user.getId().intValue()))
                .body("data.createdAt", notNullValue())
                .body("data.modifiedAt", notNullValue());
        //@formatter:on
    }

    @DisplayName("posting의 작성자가 아닌 유저가 수정 시도 시 예외를 던진다.")
    @Test
    void edit_exception() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));
        PostEditDto postEditDto = new PostEditDto("title1", "content1");

        //@formatter:off
        given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("postId", post.getId())
                .queryParam("userId", 11L)
                .body(postEditDto)
        .when()
                .patch("/posts/{postId}")
        .then()
                .log().all()
                .body("time", notNullValue())
                .body("status", equalTo(400))
                .body("msg", equalTo(ExceptionCode.NOT_MATCHING_OWNER.getMsg()));
        //@formatter:on
    }

    @DisplayName("특정 posting을 조회할 수 있다.")
    @Test
    void search() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));

        //@formatter:off
        given()
                .log().all()
        .when()
                .get("posts/{postId}", post.getId())
        .then()
                .log().all()
                .body("time", notNullValue())
                .body("data.title",equalTo("title"))
                .body("data.content",equalTo("content"))
                .body("data.userId", equalTo(user.getId().intValue()))
                .body("data.createdAt", notNullValue())
                .body("data.modifiedAt", notNullValue());
        //@formatter:on
    }

    @DisplayName("특정 posting을 삭제할 수 있다.")
    @Test
    void delete() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));

        //@formatter:off
        given()
                .log().all()
        .when()
                .delete("posts/{postId}", post.getId())
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data", equalTo("delete complete"));
        //@formatter:on
    }
}
