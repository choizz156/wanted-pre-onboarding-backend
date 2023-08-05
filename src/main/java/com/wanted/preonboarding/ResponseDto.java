package com.wanted.preonboarding;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

    private final LocalDateTime time = LocalDateTime.now();
    private final T data;

    public ResponseDto(final T data) {
        this.data = data;
    }
}
