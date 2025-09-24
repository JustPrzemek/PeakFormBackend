package com.peakform.comments.service;

import com.peakform.comments.dto.AddCommentDTO;
import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.dto.ResponseCommentDTO;
import com.peakform.comments.mapper.CommentsMapper;
import com.peakform.comments.model.Comments;
import com.peakform.comments.repository.CommentsRepository;
import com.peakform.exceptions.ResourceNotFoundException;
import com.peakform.pages.PagedResponse;
import com.peakform.posts.model.Post;
import com.peakform.posts.repository.PostRepository;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentsMapper commentsMapper;

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

    @Override
    @Transactional
    public ResponseCommentDTO addCommentForPost(AddCommentDTO addCommentDTO) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        Post post = postRepository.findById(addCommentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono posta o ID: " + addCommentDTO.getPostId()));

        Comments newComment = new Comments();
        newComment.setPost(post);
        newComment.setUser(user);
        newComment.setContent(addCommentDTO.getContent());

        Comments savedComment = commentsRepository.save(newComment);

        return commentsMapper.toResponseCommentDTO(savedComment);
    }
}
