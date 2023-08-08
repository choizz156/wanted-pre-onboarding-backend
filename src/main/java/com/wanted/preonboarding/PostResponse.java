package com.wanted.preonboarding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class PostResponse {

    private long postId;
    private String title;
    private String content;
    private String email;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private PostResponse(
        final Long postId,
        final String title,
        final String content,
        final String email,
        final Long userId,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt
    ) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.email = email;
        this.userId = userId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .email(post.getUserEmail())
            .userId(post.getUserId())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
