package com.hwans.apiserver.repository.blog;

import com.hwans.apiserver.entity.blog.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByAccountIdAndPostId(UUID accountId, UUID postId);
}
