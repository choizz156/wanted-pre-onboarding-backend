package com.wanted.preonboarding;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QueryPostService {

    private final PostRepository postRepository;

    public Post getPost(final Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);

        return optionalPost.orElseThrow(
            () -> new BusinessLoginException(ExceptionCode.NOT_FOUND_POST)
        );
    }

    public List<PostResponse> getLists(final Integer page, final Integer size) {
        PostSearching postSearching = getPostSearching(page, size);

        return postRepository.findLists(postSearching).stream()
            .map(PostResponse::of)
            .toList();
    }

    private PostSearching getPostSearching(final Integer page, final Integer size) {
        return isDefault(size)
            ? new PostSearching()
            : new PostSearching(page, size);
    }

    private boolean isDefault(final Integer size) {
        return size == null;
    }
}