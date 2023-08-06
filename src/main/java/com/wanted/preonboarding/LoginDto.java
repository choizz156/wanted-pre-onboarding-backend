package com.wanted.preonboarding;

import jakarta.validation.constraints.Pattern;

public record LoginDto(
    @Pattern(
        regexp = ".*@.*",
        message = "@가 포함되어야 합니다."
    )
    String username,
    @Pattern(
        regexp = "^.{8,}$",
        message = "비밀번호는 8자리 이상이어야 합니다."
    )
    String password
) {
}
