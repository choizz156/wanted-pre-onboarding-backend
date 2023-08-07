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
    private static final int MAX_SIZE = 1000;
    private static final int MIN_PAGE = 1;

    private int page = MIN_PAGE;
    private int size = DEFAULT_SIZE;

    private PostSearching(final int page, final int size) {
        this.page = (page == 0 ? 1 : page);
        this.size = size;
    }

    public static PostSearching of(final int page, final int size) {
        return new PostSearching(page, size);
    }

    public static PostSearching defualtInstance() {
        return new PostSearching();
    }

    public long getOffset() {
        return (long) (max(MIN_PAGE, page) - 1) * min(size, MAX_SIZE);
    }

}
