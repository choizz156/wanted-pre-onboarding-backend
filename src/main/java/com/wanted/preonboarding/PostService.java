package com.wanted.preonboarding;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public Post posting(final Long userId, final PostCreateDto dto) {
        User user = userService.findUser(userId);
        Post post = dto.toEntity();

        post.addUser(user);

        postRepository.save(post);

        log.info("post complete");
        return post;
    }

    public Post edit(final Long userId, final Long postId, final PostEditDto postEditDto) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post postEntity = optionalPost.orElseThrow(
            () -> new BusinessLoginException(ExceptionCode.NOT_FOUND_POST)
        );

        verifyOwner(userId, postEntity);

        editPost(postEditDto, postEntity);

        return postEntity;
    }

    public void delete(final Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessLoginException(ExceptionCode.NOT_FOUND_POST));

        postRepository.delete(post);
    }


    private void editPost(final PostEditDto postEditDto, final Post post) {

        if (isDifferentTitle(postEditDto, post)) {
            post.editTitle(postEditDto.title());
        }

        if (isDifferentContent(postEditDto, post)) {
            post.editContent(postEditDto.content());
        }
    }

    private boolean isDifferentContent(final PostEditDto postEditDto, final Post post) {
        return !post.getContent().equals(postEditDto.content());
    }

    private boolean isDifferentTitle(final PostEditDto postEditDto, final Post post) {
        return !post.getTitle().equals(postEditDto.title());
    }

    private void verifyOwner(final Long userId, final Post post) {
        if (isNotOwner(userId, post)) {
            throw new BusinessLoginException(ExceptionCode.NOT_MATCHING_OWNER);
        }
    }

    private boolean isNotOwner(final Long userId, final Post post) {
        return !post.getUser().getId().equals(userId);
    }

}
