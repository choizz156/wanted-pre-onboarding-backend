package com.wanted.preonboarding;

import jakarta.validation.constraints.Pattern;

public record JoinDto(
    @Pattern(
        regexp = ".*@.*",
        message = "@가 포함되어야 합니다."
    )
    String email,

    @Pattern(
        regexp = "^.{8,}$",
        message = "비밀번호는 8자리 이상이어야 합니다."
    )
    String password
) {

    public User toEntity() {
        return User.builder().email(email()).password(password()).build();
    }
}
