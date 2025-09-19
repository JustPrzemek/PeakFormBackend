package com.peakform.comments.service;

import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.repository.CommentsRepository;
import com.peakform.pages.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;

    @Override
    public PagedResponse<CommentsDTO> getCommentsForPost(int page, int size, Long postId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentsDTO> commentsPage = commentsRepository.findCommentsByPostId(postId, pageable);

        return new PagedResponse<>(
                commentsPage.getContent(),
                commentsPage.getNumber(),
                commentsPage.getSize(),
                commentsPage.getTotalElements(),
                commentsPage.getTotalPages(),
                commentsPage.isLast()
        );
    }
}
