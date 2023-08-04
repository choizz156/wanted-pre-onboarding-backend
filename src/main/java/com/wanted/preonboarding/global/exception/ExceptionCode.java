package com.wanted.preonboarding.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    EXIST_EMAIL(404,"이미 가입한 e-mail입니다.");

    private final int code;
    private final String msg;
}
