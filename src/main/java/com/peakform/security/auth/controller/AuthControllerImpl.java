package com.peakform.security.auth.controller;

import com.peakform.security.auth.util.JwtUtil;
import com.peakform.security.user.dto.AuthResponse;
import com.peakform.security.user.dto.LoginRequest;
import com.peakform.security.user.dto.LogoutRequest;
import com.peakform.security.user.dto.RegisterRequest;
import com.peakform.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @Override
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Override
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @Override
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        userService.updateRefreshToken(username, null);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        userService.verifyUserEmail(token);
        return ResponseEntity.ok().build();
    }
}