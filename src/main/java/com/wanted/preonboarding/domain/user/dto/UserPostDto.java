package com.wanted.preonboarding.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserPostDto(
    @Email
    String email,

    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "비밀번호는 알파벳 숫자 포함 8자리 이상이어야 합니다."
    )
    String password
) {
}
