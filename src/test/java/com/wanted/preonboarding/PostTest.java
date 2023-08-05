package com.wanted.preonboarding;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {
    @DisplayName("posting을 수정할 수 있다.")
    @Test
    void edit() throws Exception {
        //given
        Post post = new Post("title", "content");

        //when
        post.editContent("content1");
        post.editTitle("title1");

        //then
        assertThat(post.getContent()).isEqualTo("content1");
        assertThat(post.getTitle()).isEqualTo("title1");
    }
}
