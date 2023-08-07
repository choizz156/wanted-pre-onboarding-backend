package com.wanted.preonboarding;

import java.time.LocalDateTime;
import lombok.Builder;

public record MultiResponseDto<T>(
    LocalDateTime time,
    int page,
    Integer size,
    Sort sort,
    T data
) {

    @Builder
    public MultiResponseDto(
        final LocalDateTime time,
        final int page,
        final Integer size,
        final Sort sort,
        final T data
    ) {
        this.time = time;
        this.page = page;
        this.size = (size == null ? 10 : size);
        this.sort = (sort == null ? Sort.DESC : Sort.ASC);
        this.data = data;
    }

    public static <T> MultiResponseDto<T> of(final int page, final Integer size, final Sort sort, final T data) {
        return new MultiResponseDto<>(LocalDateTime.now(), page, size, sort, data);
    }
}
