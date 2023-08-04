package com.wanted.preonboarding.global.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record LoginDto(
    @Email(message = "이메일 형식이어야 합니다.")
    String username,
    @Pattern(
        regexp = "^.{8,}$",
        message = "비밀번호는 8자리 이상이어야 합니다."
    )
    String password
) {
}