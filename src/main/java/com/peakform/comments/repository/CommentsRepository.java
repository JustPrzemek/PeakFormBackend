package com.peakform.comments.repository;

import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.model.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {


    @Query("""
           SELECT new com.peakform.comments.dto.CommentsDTO(
               c.id, c.content, u.username, c.createdAt
           )
           FROM Comments c
           LEFT JOIN c.user u
           WHERE c.post.id = :postId
           """)
    Page<CommentsDTO> findCommentsByPostId(Long postId, Pageable pageable);

    @Query("""
        SELECT c FROM Comments c
        JOIN FETCH c.user
        WHERE c.post.id IN :postIds
        ORDER BY c.createdAt DESC
    """)
    List<Comments> findLatestCommentsForPosts(@Param("postIds") List<Long> postIds);

    Page<Comments> findByPostId(Long postId, Pageable pageable);
}
