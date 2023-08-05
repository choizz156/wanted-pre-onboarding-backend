package com.wanted.preonboarding;

import static com.wanted.preonboarding.QPost.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements QueryPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findLists(final PostSearching postSearching) {
        return jpaQueryFactory.selectFrom(post)
            .limit(postSearching.getSize())
            .offset(postSearching.getOffset())
            .orderBy(post.id.desc())
            .fetch();
    }
}
