package com.wanted.preonboarding;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class PostResponse {

    private String title;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private PostResponse(
        final String title,
        final String content,
        final Long userId,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt
    ) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder()
            .title(post.getTitle())
            .content(post.getContent())
            .userId(post.getUser().getId())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
