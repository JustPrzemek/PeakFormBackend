package com.peakform.security.auth.service;

import com.peakform.security.auth.util.JwtUtil;
import com.peakform.security.user.model.User;
import com.peakform.security.user.model.UserPrincipal;
import com.peakform.security.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // Potrzebne do zapisu refresh tokena

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Value("${https.secure}")
    private Boolean isSecure;

    @Override
    @Transactional // Upewnij się, że operacja zapisu jest w transakcji
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Generujemy tokeny na podstawie danych z UserPrincipal
        String accessToken = jwtUtil.generateToken(userPrincipal);
        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal);

        // Pobieramy użytkownika, aby zapisać refresh token
        // Używamy getUsername(), bo jest unikalny.
        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after OAuth2 login"));

        user.setRefreshToken(refreshToken);
        userRepository.save(user); // Zapisujemy tylko refresh token

        // --- POPRAWKA: Używamy ResponseCookie zamiast new Cookie() ---
        ResponseCookie jwtCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isSecure)      // Pobieramy z configu (musi być TRUE na Renderze)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")      // KLUCZOWE dla Netlify -> Render
                .build();

        // Dodajemy ciasteczko jako nagłówek Set-Cookie
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
