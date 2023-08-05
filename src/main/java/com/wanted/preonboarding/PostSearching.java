package com.wanted.preonboarding;

import static java.lang.Math.max;
import static java.lang.Math.min;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSearching {

    private int page = 1;
    private int size = 10;

    private static final int MAX_SIZE = 1000;
    private static final int MIN_PAGE = 1;

    public PostSearching(final int page, final int size) {
        this.page = (page == 0 ? 1 : page);
        this.size = size;
    }

    public long getOffset() {
        return (long) (max(MIN_PAGE, page) - 1) * min(size, MAX_SIZE);
    }
}
