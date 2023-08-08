package com.wanted.preonboarding;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;
    private final QueryPostService queryPostService;

    private static final String DELETE_COMPLETE = "delete complete";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponseDto<PostResponse> post(
        @RequestParam Long userId,
        @RequestBody PostCreateDto postCreateDto
    ) {
        Post post = postService.posting(userId, postCreateDto);
        return new SingleResponseDto<>(PostResponse.of(post));
    }

    @PatchMapping("/{postId}")
    public SingleResponseDto<PostResponse> edit(
        @PathVariable Long postId,
        @RequestParam Long userId,
        @RequestBody PostEditDto postEditDto
    ) {
        Post edit = postService.edit(userId, postId, postEditDto);
        return new SingleResponseDto<>(PostResponse.of(edit));
    }

    @GetMapping("/{postId}")
    public SingleResponseDto<PostResponse> search(@PathVariable Long postId) {
        Post post = queryPostService.getPost(postId);
        return new SingleResponseDto<>(PostResponse.of(post));
    }

    @GetMapping
    public MultiResponseDto<List<PostResponse>> paging(
        @RequestParam @Min(1) int page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) Sort sort
    ) {
        List<PostResponse> lists = queryPostService.getLists(page, size, sort);
        return MultiResponseDto.of(page, lists.size(), sort, lists);
    }

    @DeleteMapping("/{postId}")
    public SingleResponseDto<String> delete(
        @PathVariable Long postId,
        @RequestParam Long userId
    ) {
        postService.delete(userId, postId);
        return new SingleResponseDto<>(DELETE_COMPLETE);
    }
}
