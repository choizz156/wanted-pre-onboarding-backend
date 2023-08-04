package com.wanted.preonboarding.web.dto;

import com.wanted.preonboarding.domain.post.entity.Post;

public record PostCreateDto(String title, String content) {

    public Post toEntity(){
        return Post.builder().title(title()).content(content()).build();
    }
}
