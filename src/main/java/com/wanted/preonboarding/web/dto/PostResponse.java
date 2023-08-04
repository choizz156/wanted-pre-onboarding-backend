package com.wanted.preonboarding.web.dto;

import com.wanted.preonboarding.domain.post.entity.Post;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class PostResponse {

    private LocalDateTime createdAt = LocalDateTime.now();
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
