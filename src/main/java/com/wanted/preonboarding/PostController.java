package com.wanted.preonboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    private static final String DELETE_COMPLETE = "delete complete";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<PostResponse> post(
        @RequestParam Long userId,
        @RequestBody PostCreateDto postCreateDto
    ) {
        Post post = postService.posting(userId, postCreateDto);
        return new ResponseDto<>(PostResponse.of(post));
    }

    @PatchMapping("/{postId}")
    public ResponseDto<PostResponse> edit(
        @PathVariable Long postId,
        @RequestParam Long userId,
        @RequestBody PostEditDto postEditDto
    ) {
        Post edit = postService.edit(userId, postId, postEditDto);
        return new ResponseDto<>(PostResponse.of(edit));
    }

    @GetMapping("/{postId}")
    public ResponseDto<PostResponse> search(@PathVariable Long postId) {
        Post post = postService.getPost(postId);
        return new ResponseDto<>(PostResponse.of(post));
    }

    @DeleteMapping("/{postId}")
    public ResponseDto<String> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return new ResponseDto<>(DELETE_COMPLETE);
    }
}
