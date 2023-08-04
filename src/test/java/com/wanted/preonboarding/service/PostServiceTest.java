package com.wanted.preonboarding.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.wanted.preonboarding.domain.post.entity.Post;
import com.wanted.preonboarding.domain.post.repository.PostRepository;
import com.wanted.preonboarding.domain.post.service.PostService;
import com.wanted.preonboarding.domain.user.entity.User;
import com.wanted.preonboarding.domain.user.repository.UserRepository;
import com.wanted.preonboarding.domain.user.service.UserService;
import com.wanted.preonboarding.web.dto.JoinDto;
import com.wanted.preonboarding.web.dto.PostCreateDto;
import org.junit.jupiter.api.AfterEach;
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
        JoinDto joinDto = new JoinDto("test@gmail.com", "123456");
        user = userService.signUp(joinDto);
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("게시글을 저장할 수 있다.")
    @Test
    void post_save() throws Exception {
        //given
        PostCreateDto dto = new PostCreateDto("title", "content");

        //when
        postService.posting(user.getId(), dto);

        //then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getId()).isEqualTo(user.getId());
        assertThat(post.getTitle()).isEqualTo("title");
        assertThat(post.getContent()).isEqualTo("content");
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
