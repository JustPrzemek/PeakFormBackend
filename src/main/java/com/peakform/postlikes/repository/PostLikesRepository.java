package com.peakform.postlikes.repository;

import com.peakform.postlikes.model.PostLikes;
import com.peakform.posts.model.Post;
import com.peakform.security.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {
    boolean existsByUserAndPost(User user, Post post);

    long countByPost(Post post);

    Optional<PostLikes> findByUserAndPost(User user, Post post);
}
