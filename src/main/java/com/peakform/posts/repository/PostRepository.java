package com.peakform.posts.repository;

import com.peakform.posts.dto.PostDTO;
import com.peakform.posts.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT new com.peakform.posts.dto.PostDTO(p.id, p.content, p.mediaUrl, p.mediaType, p.createdAt, " +
            "COUNT(DISTINCT l.id), COUNT(DISTINCT c.id)) " +
            "FROM Post p " +
            "LEFT JOIN PostLikes l ON l.post = p " +
            "LEFT JOIN Comments c ON c.post = p " +
            "WHERE p.user.id = :userId " +
            "GROUP BY p")
    Page<PostDTO> findPostsWithStatsByUserId(@Param("userId")  Long userId, Pageable pageable);

    @Query("""
        SELECT new com.peakform.posts.dto.PostDTO(
            p.id, p.content, p.mediaUrl, p.mediaType, p.createdAt,
            COUNT(DISTINCT l.id), COUNT(DISTINCT c.id)
        )
        FROM Post p
        LEFT JOIN PostLikes l ON l.post = p
        LEFT JOIN Comments c ON c.post = p
        WHERE p.user.id IN (
            SELECT f.followed.id FROM Followers f WHERE f.follower.id = :userId
        )
        GROUP BY p
    """)
    Page<PostDTO> findPostsFromFollowedUsers(@Param("userId") Long userId, Pageable pageable);


    @Query("""
        SELECT new com.peakform.posts.dto.PostDTO(
            p.id, p.content, p.mediaUrl, p.mediaType, p.createdAt,
            COUNT(DISTINCT l.id), COUNT(DISTINCT c.id)
        )
        FROM Post p
        LEFT JOIN PostLikes l ON l.post = p
        LEFT JOIN Comments c ON c.post = p
        WHERE p.user.username = :username
        GROUP BY p
    """)
    Page<PostDTO> findPostsWithStatsByUsername(@Param("username") String username, Pageable pageable);

}
