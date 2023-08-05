package com.wanted.preonboarding;

import java.util.List;

public interface QueryPostRepository {

    List<Post> findLists(PostSearching postSearching);
}
