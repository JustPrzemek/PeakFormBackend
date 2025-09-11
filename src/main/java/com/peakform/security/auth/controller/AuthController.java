package com.peakform.security.auth.controller;

import com.peakform.security.user.dto.AuthResponse;
import com.peakform.security.user.dto.LoginRequest;
import com.peakform.security.user.dto.LogoutRequest;
import com.peakform.security.user.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API")
public interface AuthController {

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    ResponseEntity<String> register(@RequestBody RegisterRequest request);

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request);

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh token",
            description = "Generates a new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request);

    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Delete token")
    ResponseEntity<Void> logout(@RequestBody LogoutRequest request);

    @GetMapping("/verify")
    @Operation(
            summary = "Verify email",
            description = "Verifies user's email using verification token")
    ResponseEntity<String> verifyEmail(@RequestParam("token") String token);
}
