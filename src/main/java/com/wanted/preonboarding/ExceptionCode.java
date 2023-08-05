package com.wanted.preonboarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    EXIST_EMAIL(400, "이미 가입한 e-mail입니다."),
    NOT_FOUND_USER(400, "찾을 수 없는 회원입니다."),
    NOT_FOUND_POST(400, "찾을 수 없는 게시글입니다." ),
    NOT_MATCHING_OWNER(403, "작성자와 동일한 회원이 아닙니다." );

    private final int code;
    private final String msg;
}
