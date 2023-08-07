package com.wanted.preonboarding;

import java.time.LocalDateTime;
import lombok.Builder;

public record MultiResponseDto<T>(
    LocalDateTime time,
    int page,
    Integer size,
    T data
) {

    @Builder
    public MultiResponseDto(
        final LocalDateTime time,
        final int page,
        final Integer size,
        final T data
    ) {
        this.time = time;
        this.page = page;
        this.size = (size == null) ? 10 : size;
        this.data = data;
    }

    public static <T> MultiResponseDto<T> of(final int page, final Integer size, final T data) {
        return new MultiResponseDto<>(LocalDateTime.now(), page, size, data);
    }
}
