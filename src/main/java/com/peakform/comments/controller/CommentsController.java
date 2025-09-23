package com.peakform.comments.controller;

import com.peakform.comments.dto.AddCommentDTO;
import com.peakform.comments.dto.CommentsDTO;
import com.peakform.pages.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Comments for posts")
public interface CommentsController {

    @GetMapping("/getCommentsForPost")
    @Operation(
            summary = "",
            description = ""
    )
    ResponseEntity<PagedResponse<CommentsDTO>> getCommentsForPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long postId

    );

    @PostMapping("/addCommentForPost")
    @Operation(
            summary = "",
            description = ""
    )
    ResponseEntity<CommentsDTO> addComment(@Valid @RequestBody AddCommentDTO addCommentDTO);
}
