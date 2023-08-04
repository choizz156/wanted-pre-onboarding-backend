package com.wanted.preonboarding.global.exception;

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
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getBindingResult());
    }

    @ExceptionHandler(BusinessLoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse businessLogicExceptionHandler(BusinessLoginException e) {
        return ErrorResponse.of(e.getMessage());
    }
}
