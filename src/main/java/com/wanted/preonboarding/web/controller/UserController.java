package com.wanted.preonboarding.web.controller;

import com.wanted.preonboarding.domain.user.service.UserService;
import com.wanted.preonboarding.web.dto.ResponseDto;
import com.wanted.preonboarding.web.dto.UserPostDto;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<String> post(@Valid @RequestBody UserPostDto userPostDto) {
        userService.signUp(userPostDto);
        return new ResponseDto<>("회원 가입 완료");
    }
}
