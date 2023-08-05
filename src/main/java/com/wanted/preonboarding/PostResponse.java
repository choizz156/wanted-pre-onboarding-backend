package com.wanted.preonboarding;

import lombok.Builder;
import lombok.Data;

@Data
public class PostResponse {

    private String title;
    private String content;

    @Builder
    private PostResponse(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder().title(post.getTitle()).content(post.getContent()).build();
    }
}
