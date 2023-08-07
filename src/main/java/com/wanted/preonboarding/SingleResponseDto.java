package com.wanted.preonboarding;

import java.time.LocalDateTime;

public record SingleResponseDto<T>(
    LocalDateTime time,
    T data
) {

    public SingleResponseDto(final T data) {
        this(LocalDateTime.now(), data);
    }
}
