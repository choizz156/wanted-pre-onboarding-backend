package com.wanted.preonboarding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleResponseDto<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return new SingleResponseDto<>(ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getBindingResult()));
    }

    @ExceptionHandler(BusinessLoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleResponseDto<ErrorResponse> businessLogicExceptionHandler(BusinessLoginException e) {
        return new SingleResponseDto<>(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleResponseDto<ErrorResponse> runtimeExceptionHandler(RuntimeException e) {
        return new SingleResponseDto<>(ErrorResponse.of(e.getMessage()));
    }
}
