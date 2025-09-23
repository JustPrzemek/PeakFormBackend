package com.peakform.comments.controller;

import com.peakform.comments.dto.AddCommentDTO;
import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.service.CommentsService;
import com.peakform.pages.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentsControllerImpl implements CommentsController {

    private final CommentsService commentsService;

    @Override
    public ResponseEntity<PagedResponse<CommentsDTO>> getCommentsForPost(int page, int size, Long postId) {
        return ResponseEntity.ok(commentsService.getCommentsForPost(page, size, postId));
    }

    @Override
    public ResponseEntity<CommentsDTO> addComment(AddCommentDTO addCommentDTO) {
        CommentsDTO createdComment = commentsService.addCommentForPost(addCommentDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
}
