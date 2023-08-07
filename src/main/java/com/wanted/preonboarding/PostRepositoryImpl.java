package com.wanted.preonboarding;

import static com.wanted.preonboarding.QPost.post;

import com.querydsl.core.types.OrderSpecifier;
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
            .orderBy(getSort(postSearching))
            .fetch();
    }

    private OrderSpecifier<Long> getSort(final PostSearching postSearching) {
        return postSearching.getSort() == Sort.DESC ? post.id.desc() : post.id.asc();
    }
}
