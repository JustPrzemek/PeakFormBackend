package com.peakform.security.auth.service;

import com.peakform.security.auth.util.JwtUtil;
import com.peakform.security.user.model.User;
import com.peakform.security.user.model.UserPrincipal;
import com.peakform.security.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
