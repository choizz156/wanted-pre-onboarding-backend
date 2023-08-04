package com.wanted.preonboarding.domain.post.repository;

import com.wanted.preonboarding.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
