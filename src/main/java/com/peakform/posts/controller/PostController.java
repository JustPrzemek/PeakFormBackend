package com.peakform.posts.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.posts.dto.FollowersPostsDTO;
import com.peakform.posts.dto.PostDetailsDTO;
import com.peakform.posts.dto.PostDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Posts")
public interface PostController {

    @GetMapping("/myPosts")
    @Operation(
            summary = "Pobiera posty zalogowanego użytkownika",
            description = "Zwraca listę postów wraz z licznikami lajków i komentarzy z paginacją"
    )
    ResponseEntity<PagedResponse<PostDTO>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping("/feed")
    @Operation(
            summary = "Pobiera posty obserwowanych użytkowników",
            description = "Zwraca strumień postów od osób, które obserwuje zalogowany użytkownik"
    )
    ResponseEntity<PagedResponse<FollowersPostsDTO>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping("/users/{username}/posts")
    @Operation(
            summary = "Posty na profilu użytkownika",
            description = "Zwraca posty wybranego użytkownika, jeśli zalogowany użytkownik go obserwuje"
    )
    ResponseEntity<PagedResponse<PostDTO>> getUserPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @PostMapping(path = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create a new post",
            description = "Creates a new post with text content and an optional image/video file."
    )
    ResponseEntity<String> createPost(
            @Parameter(description = "Text content of the post", required = true)
            @RequestPart("content") String content,

            @Parameter(description = "Post image or video file (optional)")
            @RequestPart(value = "file", required = false) MultipartFile file);

    @Operation(
            summary = "Get Full Post Details",
            description = "Fetches all details for a single post including its comments, paginated."
    )
    @GetMapping("/{postId}")
    ResponseEntity<PostDetailsDTO> getPostDetails(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

}
