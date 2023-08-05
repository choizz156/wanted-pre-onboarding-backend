package com.wanted.preonboarding;

import com.wanted.preonboarding.UserService;
import com.wanted.preonboarding.ResponseDto;
import com.wanted.preonboarding.JoinDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static final String JOIN_COMPLETE = "회원 가입 완료";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<String> post(@Valid @RequestBody JoinDto joinDto) {
        userService.signUp(joinDto);
        return new ResponseDto<>(JOIN_COMPLETE);
    }
}
