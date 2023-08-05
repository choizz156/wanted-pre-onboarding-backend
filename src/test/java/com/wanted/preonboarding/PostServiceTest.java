package com.wanted.preonboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        JoinDto joinDto = new JoinDto("test@gmail.com", "12345678");
        user = userService.signUp(joinDto);
    }

    @DisplayName("게시글을 저장할 수 있다.")
    @Test
    void post_save() throws Exception {
        //given
        PostCreateDto dto = new PostCreateDto("title", "content");

        //when
        postService.posting(user.getId(), dto);

        //then
        List<Post> posts = postRepository.findAll();

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("title");
        assertThat(posts.get(0).getContent()).isEqualTo("content");
    }

    @Transactional
    @DisplayName("게시글 저장 시 엔티티 간의 연관관계가 성립된다.")
    @Test
    void post_entity() throws Exception {
        //given
        PostCreateDto dto = new PostCreateDto("title", "content");

        //when
        postService.posting(user.getId(), dto);

        //then
        Post post = postRepository.findAll().get(0);

        assertThat(post.getUser().getId()).isEqualTo(user.getId());
        assertThat(post.getUser().getPosts()).hasSize(1);
        assertThat(post.getUser().getPosts())
            .extracting("id", "title", "content")
            .containsAnyOf(
                tuple(post.getId(), "title", "content")
            );
    }

    @DisplayName("게시글 저장 시 유저를 찾지 못하면 예외를 던진다.")
    @Test
    void post_exception() throws Exception {

        //given
        PostCreateDto dto = new PostCreateDto("title", "content");

        //expected
        assertThatThrownBy(() -> postService.posting(14L, dto))
            .isInstanceOf(BusinessLoginException.class)
            .hasMessageContaining(ExceptionCode.NOT_FOUND_USER.getMsg());
    }

    @DisplayName("posting 내용은 작성자만이 수정할 수 있다.")
    @Test
    void edit() throws Exception {
        //given
        PostCreateDto dto = new PostCreateDto("title", "content");
        Post posting = postService.posting(user.getId(), dto);

        PostEditDto postEditDto = new PostEditDto("title1", "content1");

        //when
        Post edit = postService.edit(user.getId(), posting.getId(), postEditDto);

        //then
        assertThat(edit.getId()).isEqualTo(posting.getId());
        assertThat(edit.getContent()).isEqualTo("content1");
        assertThat(edit.getTitle()).isEqualTo("title1");
    }

    @DisplayName("작성자가 아닌 유저가 포스팅을 수정할 경우 예외가 발생한다.")
    @Test
    void edit_exception() throws Exception {
        //given
        PostCreateDto dto = new PostCreateDto("title", "content");
        Post posting = postService.posting(user.getId(), dto);
        PostEditDto postEditDto = new PostEditDto("title1", "content1");
        Long postingId = posting.getId();

        //when
        assertThatCode(() ->
            postService.edit(11L, postingId, postEditDto)
        )
            .isInstanceOf(BusinessLoginException.class)
            .hasMessageContaining(ExceptionCode.NOT_MATCHING_OWNER.getMsg());
    }

    @DisplayName("posting 수정 시 posting이 존재하지 않으면 예외를 던진다.")
    @Test
    void edit_exception2() throws Exception {
        //given
        PostCreateDto dto = new PostCreateDto("title", "content");
        Post posting = postService.posting(user.getId(), dto);
        PostEditDto postEditDto = new PostEditDto("title1", "content1");
        Long postingId = posting.getId();
        Long userId = user.getId();

        assertThatCode(() ->
            postService.edit(userId, 22L, postEditDto)
        )
            .isInstanceOf(BusinessLoginException.class)
            .hasMessageContaining(ExceptionCode.NOT_FOUND_POST.getMsg());

    }

    @DisplayName("특정 posting을 조회할 수 있다.")
    @Test
    void search() throws Exception {
        //given
        Post post = postRepository.save(new Post("title", "content"));

        //when
        Post result = postService.getPost(post.getId());

        //then
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContent()).isEqualTo("content");
    }

    @DisplayName("특정 posting을 조회 시 존재하지 않는 경우 예외를 던진다.")
    @Test
    void search_exception() throws Exception {
        //given
        postRepository.save(new Post("title", "content"));

        //then
        assertThatThrownBy(() -> postService.getPost(11L))
            .isInstanceOf(BusinessLoginException.class)
            .hasMessageContaining(ExceptionCode.NOT_FOUND_POST.getMsg());
    }

    @DisplayName("특정 posting을 삭제할 수 있다.")
    @Test
    void delete() throws Exception {
        //given
        Post post = postRepository.save(new Post("title", "content"));
        //when
        postService.delete(post.getId());

        //then
        List<Post> all = postRepository.findAll();
        assertThat(all).isEmpty();
    }
}
