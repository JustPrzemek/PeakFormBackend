package com.peakform.security.auth.controller;

import com.peakform.security.user.dto.AuthResponse;
import com.peakform.security.user.dto.LoginRequest;
import com.peakform.security.user.dto.LogoutRequest;
import com.peakform.security.user.dto.RegisterRequest;
import com.peakform.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final UserService userService;

    @Value("${https.secure}")
    private Boolean isSecure;

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)    // JS tego nie widzi (ochrona przed XSS)
                .secure(isSecure)     // Zmień na TRUE, jeśli masz HTTPS (na produkcji obowiązkowo)
                .path("/api/auth/refresh") // Ciasteczko wysyłane TYLKO do endpointu odświeżania (i logout)
                .maxAge(7 * 24 * 60 * 60) // 7 dni
                .sameSite("None") // Ochrona CSRF
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("None")
                .build();
    }

    @Override
    public ResponseEntity<String> register(RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        AuthResponse authResponse = userService.login(request);

        ResponseCookie cookie = createRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(authResponse.getAccessToken(), null)); // null, bo nie chcemy go w JSON
    }

    @Override
    public ResponseEntity<AuthResponse> refresh(
            String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Musisz lekko dostosować userService.refresh, żeby przyjmował Stringa, a nie Mapę
        // Albo spakuj go w mapę tutaj, jeśli nie chcesz ruszać serwisu:
        Map<String, String> tokenMap = Map.of("refreshToken", refreshToken);

        AuthResponse authResponse = userService.refresh(tokenMap);

        // Przy odświeżaniu, warto odświeżyć też ciasteczko (rotacja tokenów)
        ResponseCookie cookie = createRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(authResponse.getAccessToken(), null));
    }

    @Override
    public ResponseEntity<Void> logout(LogoutRequest request) {
        ResponseCookie cookie = deleteRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Override
    public ResponseEntity<String> verifyEmail(String token) {
        userService.verifyUserEmail(token);
        return ResponseEntity.ok().build();
    }
}