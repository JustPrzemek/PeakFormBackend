package com.peakform.posts.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.posts.dto.PostDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    ResponseEntity<PagedResponse<PostDTO>> getFeed(
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

}
