package com.wanted.preonboarding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class ErrorResponse {

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final int status;
    private final String msg;
    private final List<CustomFieldError> customFieldErrors;

    @Builder
    private ErrorResponse(int status, String msg, List<CustomFieldError> customFieldErrors) {
        this.status = status;
        this.msg = msg;
        this.customFieldErrors = customFieldErrors;
    }

    public static ErrorResponse of(HttpStatus httpStatus, BindingResult bindingResult) {
        return ErrorResponse.builder()
            .status(httpStatus.value())
            .customFieldErrors(CustomFieldError.of(bindingResult))
            .build();
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return ErrorResponse.builder()
            .status(exceptionCode.getCode())
            .msg(exceptionCode.getMsg())
            .build();
    }

    public static ErrorResponse of(HttpStatus httpStatus, String msg) {
        return ErrorResponse.builder()
            .status(httpStatus.value())
            .msg(msg)
            .build();
    }

    public static ErrorResponse of(String msg) {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .msg(msg)
            .build();
    }

    @Getter
    public static class CustomFieldError {

        private String field;
        private Object rejectedValue;
        private String reason;

        private CustomFieldError(
            final String field,
            final Object rejectedValue,
            final String reason
        ) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<CustomFieldError> of(BindingResult bindingResult) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error ->
                    new CustomFieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                    )
                )
                .toList();
        }
    }
}
