package com.wanted.preonboarding;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = PRIVATE)
public class PostSearching {

    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;
    private static final int MIN_PAGE = 1;
    private static final Sort DEFAULT_SORT = Sort.DESC;

    private int page;
    private Integer size;
    private Sort sort;

    private PostSearching(final int page, final Integer size, final Sort sort) {
        this.page = (page == 0 ? MIN_PAGE : page);
        this.size = getSize(size);
        this.sort = (sort == null ? Sort.DESC : Sort.ASC);
    }

    public static PostSearching of(final int page, final Integer size, final Sort sort) {
        return new PostSearching(page, size, sort);
    }

    public long getOffset() {
        return (long) (max(MIN_PAGE, page) - 1) * min(size, MAX_SIZE);
    }

    private int getSize(final Integer size) {
        if (size == null || size < DEFAULT_SIZE) {
            return DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            return MAX_SIZE;
        }

        return size;
    }
}
