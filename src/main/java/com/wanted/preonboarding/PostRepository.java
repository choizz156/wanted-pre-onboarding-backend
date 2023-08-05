package com.wanted.preonboarding;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, QueryPostRepository {

}
