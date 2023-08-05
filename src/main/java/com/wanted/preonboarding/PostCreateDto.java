package com.wanted.preonboarding;

public record PostCreateDto(String title, String content) {

    public Post toEntity(){
        return Post.builder().title(title()).content(content()).build();
    }
}
