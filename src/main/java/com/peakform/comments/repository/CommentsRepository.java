package com.peakform.comments.repository;

import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.model.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {


    @Query("""
           SELECT new com.peakform.comments.dto.CommentsDTO(
               c.id, u.username, c.content, c.createdAt
           )
           FROM Comments c
           LEFT JOIN c.user u
           WHERE c.post.id = :postId
           """)
    Page<CommentsDTO> findCommentsByPostId(Long postId, Pageable pageable);
}
