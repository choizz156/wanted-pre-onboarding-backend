package com.wanted.preonboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.wanted.preonboarding.JoinDto;
import com.wanted.preonboarding.PostCreateDto;
import com.wanted.preonboarding.Post;
import com.wanted.preonboarding.PostRepository;
import com.wanted.preonboarding.PostService;
import com.wanted.preonboarding.User;
import com.wanted.preonboarding.UserRepository;
import com.wanted.preonboarding.UserService;
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
        JoinDto joinDto = new JoinDto("test@gmail.com", "123456");
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

}
