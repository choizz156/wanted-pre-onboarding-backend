package com.wanted.preonboarding.global.security.handler;

import com.wanted.preonboarding.global.security.util.Mapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponser {

    private int status;
    private String msg;

    @Builder
    private ErrorResponser(final int status, final String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static void sendError(
        HttpServletResponse response,
        Exception exception,
        HttpStatusCode statusCode
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode.value());
        response.setCharacterEncoding("UTF-8");

        ErrorResponser errorInfo = ErrorResponser.builder()
            .msg(exception.getMessage())
            .status(statusCode.value())
            .build();

        String errorResponse = Mapper.getInstance().writeValueAsString(errorInfo);

        response.getWriter().write(errorResponse);
    }

}
