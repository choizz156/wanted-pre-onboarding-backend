package com.wanted.preonboarding;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.IntStream;
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

    @Autowired
    private UserService userService;

    private User user;
    private String token;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @BeforeEach
    void setUpEach() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        token = login();
    }

    @DisplayName("posting 생성")
    @Test
    void post() throws Exception {
        //given
        PostCreateDto postCreateDto = new PostCreateDto("title", "content");

        //@formatter:off
        given()
                .log().all()
                .header("Authorization", token)
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
                .body("data.email", equalTo(user.getEmail()))
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
                .header("Authorization", token)
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
                .body("data.email", equalTo(user.getEmail()))
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
                .header("Authorization", token)
                .pathParam("postId", post.getId())
                .queryParam("userId", 11L)
                .body(postEditDto)
        .when()
                .patch("/posts/{postId}")
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("time", notNullValue())
                .body("data.status", equalTo(400))
                .body("data.msg", equalTo(ExceptionCode.NOT_MATCHING_OWNER.getMsg()));
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
                .body("data.email", equalTo(user.getEmail()))
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
                .queryParam("userId", user.getId())
                .header("Authorization", token)
        .when()
                .delete("/posts/{postId}", post.getId())
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data", equalTo("delete complete"));
        //@formatter:on
    }

    @DisplayName("posting 작성자가 아닌 다른 유저가 삭제를 시도할 시 예외를 던진다.")
    @Test
    void delete_exception() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));

        //@formatter:off
        given()
                .log().all()
                .header("Authorization", token)
                .queryParam("userId", 12312L)
        .when()
                .delete("/posts/{postId}", post.getId())
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("time", notNullValue())
                .body("data.status", equalTo(400))
                .body("data.msg", equalTo(ExceptionCode.NOT_MATCHING_OWNER.getMsg()));
        //@formatter:on
    }

    @DisplayName("posting 목록을 조회할 경우, 1 페이지 당 10개씩 페이지네이션이 된다.(기본값)")
    @Test
    void pagination() throws Exception {
        IntStream.range(0, 30).forEach(
            i -> {
                PostCreateDto postCreateDto = fixtureMonkey.giveMeBuilder(PostCreateDto.class)
                    .set("title", "title" + i)
                    .set("content", "content" + i)
                    .sample();
                postService.posting(user.getId(), postCreateDto);
            }
        );

        //@formatter:off
        given()
                .log().all()
                .queryParam("page", 0)
        .when()
                .get("/posts")
        .then()
                .log().all()
                .body("data", hasSize(10))
                .body("data[0].title",equalTo("title29"))
                .body("data[9].title",equalTo("title20"))
                .statusCode(HttpStatus.OK.value());
        //@formatter:on
    }

    @DisplayName("사용자가 posting 목록의 갯수를 정할 시 사용자가 정한 갯수대로 페이지네이션된다.")
    @Test
    void pagination2() throws Exception {
        IntStream.range(0, 30).forEach(
            i -> {
                PostCreateDto postCreateDto = fixtureMonkey.giveMeBuilder(PostCreateDto.class)
                    .set("title", "title" + i)
                    .set("content", "content" + i)
                    .sample();
                postService.posting(user.getId(), postCreateDto);
            }
        );

        //@formatter:off
        given()
                .log().all()
                .queryParam("page", 0)
                .queryParam("size",20)
        .when()
                .get("/posts")
        .then()
                .log().all()
                .body("data", hasSize(20))
                .body("data[0].title",equalTo("title29"))
                .body("data[19].title",equalTo("title10"))
                .statusCode(HttpStatus.OK.value());
        //@formatter:on

    }


    private String login(){
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

        return response.header("Authorization");
    }
}
