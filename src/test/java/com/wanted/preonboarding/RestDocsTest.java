package com.wanted.preonboarding;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.IntStream;
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
import org.springframework.restdocs.payload.JsonFieldType;


@DisplayName("api docs test")
class RestDocsTest extends RestDocsSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    private User user;
    private String token;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        token = getToken();
    }

    @DisplayName("회원 가입")
    @Test
    void joinApi() throws Exception {
        //given
        JoinDto joinDto = new JoinDto("test1@gmail.com", "1233456777");

        //expected
        //@formatter:off
        given(super.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(joinDto)
            .filter(document("user",
                        preprocessRequest(
                            modifyUris().scheme("http").host("localhost").removePort(),
                            prettyPrint()
                        ),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                        ),
                        responseFields(
                            fieldWithPath("time").ignored(),
                            fieldWithPath("data").type(JsonFieldType.STRING).description("가입 완료 이메일")
                        )
                    )
            )
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
        JoinDto dto = new JoinDto("test1@gmail.com", "testdfdf11");
        userService.signUp(dto);

        //when
        JoinDto dto2 = new JoinDto("test1@gmail.com", "testdfd231");

        //expected
        //@formatter:off
        given(super.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto2)
            .filter(document("user-error",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                    )
                )

            )
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
        userService.signUp(new JoinDto("test2@gmail.com", "1234456778"));
        LoginDto loginDto = new LoginDto("test@gmail.com", "1234456778");

        //@formatter:off
        given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginDto)
            .filter(document("user-login",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                    ),
                    responseHeaders(
                        headerWithName("Authorization").description("인증 토큰")
                    )
                )
            )
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
        JoinDto joinDto = new JoinDto("test1@gmail.com", "12345678");
        userService.signUp(joinDto);

        //@formatter:off
        given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginDto)
            .filter(document("user-login-error",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                    )
                )
            )
        .when()
            .post("/users/login")
        .then()
            .log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("data.status", equalTo(401))
            .body("data.msg", is(in(new String[]{"자격 증명에 실패하였습니다.","Bad credentials"})));
        //@formatter:on
    }

    @DisplayName("posting 생성")
    @Test
    void post() throws Exception {
        //given
        PostCreateDto postCreateDto = new PostCreateDto("title", "content");

        //@formatter:off
        given(super.spec)
            .log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("userId", user.getId())
            .body(postCreateDto)
            .filter(document("post",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                      headerWithName("Authorization").description("인증 토큰")
                    ),
                    queryParameters(
                        parameterWithName("userId").description("작성자 이메일")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                    ),
                    responseFields(
                        fieldWithPath("time").ignored(),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("작성자 이메일"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                )
            )
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
        given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", token)
            .pathParam("postId", post.getId())
            .queryParam("userId", user.getId())
            .body(postEditDto)
            .filter(document("post-edit",
                            preprocessRequest(
                                modifyUris().scheme("http").host("localhost").removePort(),
                                prettyPrint()
                            ),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                headerWithName("Authorization").description("인증 토큰")
                            ),
                            queryParameters(
                                parameterWithName("userId").description("작성자 이메일")
                            ),
                            pathParameters(
                              parameterWithName("postId").description("포스팅 아이디")
                            ),
                            requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정할 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 내용")
                            ),
                            responseFields(
                                fieldWithPath("time").ignored(),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("수정된 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("수정된 내용"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("작성자 이메일"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                            )
                )
        )
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
        given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", token)
            .pathParam("postId", post.getId())
            .queryParam("userId", 1231L)
            .body(postEditDto)
            .filter(document("post-edit-error",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint())
                )
            )
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
        given(super.spec)
            .log().all()
            .filter(document("post-get",
                preprocessRequest(
                    modifyUris().scheme("http").host("localhost").removePort(),
                    prettyPrint()
                ),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("postId").description("포스팅 아이디")
                ),
                responseFields(
                    fieldWithPath("time").ignored(),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("data.email").type(JsonFieldType.STRING).description("작성자 이메일"),
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                )
            )
        )
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
        given(super.spec)
            .log().all()
            .queryParam("page", 1)
            .filter(document("post-page",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("page").description("페이지(1부터 시작)")
                    ),
                    responseFields(
                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("포스팅 갯수"),
                        fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호 갯수"),
                        fieldWithPath("time").ignored(),
                        subsectionWithPath("data").ignored()
                    )
                )
            )
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
        given(spec)
            .log().all()
            .queryParam("page", 1)
            .queryParam("size",5)
            .filter(document("post-page2",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("page").description("페이지(1부터 시작)"),
                        parameterWithName("size").description("유저가 원하는 사이즈").optional()
                    ),
                    responseFields(
                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("포스팅 갯수"),
                        fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호 갯수"),
                        fieldWithPath("time").ignored(),
                        subsectionWithPath("data").ignored()
                    )
                )
            )
        .when()
            .get("/posts")
        .then()
            .log().all()
            .body("data", hasSize(5))
            .body("data[0].title",equalTo("title29"))
            .body("data[4].title",equalTo("title25"))
            .statusCode(HttpStatus.OK.value());
        //@formatter:on
    }

    @DisplayName("특정 posting을 삭제할 수 있다.")
    @Test
    void delete() throws Exception {
        //given
        Post post = postService.posting(user.getId(), new PostCreateDto("title", "content"));

        //@formatter:off
        given(super.spec)
            .log().all()
            .header("Authorization", token)
            .queryParam("userId", user.getId())
            .filter(document("post-delete",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("인증 토큰")
                    ),
                    queryParameters(
                      parameterWithName("userId").description("유저 아이디")
                    ),
                    pathParameters(
                        parameterWithName("postId").description("포스팅 아이디")
                    )
                )
            )
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
        given(super.spec)
            .log().all()
            .header("Authorization", token)
            .queryParam("userId", 12312L)
            .filter(document("post-delete-error",
                    preprocessRequest(
                        modifyUris().scheme("http").host("localhost").removePort(),
                        prettyPrint()
                    ),
                    preprocessResponse(prettyPrint())
                )
            )
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

        return response.header("Authorization");
    }
}
