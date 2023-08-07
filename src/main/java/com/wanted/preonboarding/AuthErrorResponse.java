package com.wanted.preonboarding;

import static lombok.AccessLevel.PRIVATE;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
@Getter
@NoArgsConstructor(access = PRIVATE)
public class AuthErrorResponse {

    public static void sendError(
        HttpServletResponse response,
        Exception exception,
        HttpStatus status
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorInfo = ErrorResponse.of(status, exception.getMessage());

        String errorResponse =
            Mapper.getInstance().writeValueAsString(new ResponseDto<>(errorInfo));

        response.getWriter().write(errorResponse);
    }

}
