package com.wanted.preonboarding.web.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ResponseDto<T> {

    private LocalDateTime createdAt = LocalDateTime.now();
    private T data;

    public ResponseDto(final T data) {
        this.data = data;
    }
}
