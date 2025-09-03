package com.peakform.auth.controller;

import com.peakform.auth.util.JwtUtil;
import com.peakform.user.dto.AuthResponse;
import com.peakform.user.dto.LoginRequest;
import com.peakform.user.dto.LogoutRequest;
import com.peakform.user.dto.RegisterRequest;
import com.peakform.user.model.User;
import com.peakform.user.repository.UserRepository;
import com.peakform.user.service.CustomUserDetailService;
import com.peakform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CustomUserDetailService customUserDetailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("Użytkownik zarejestrowany!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        userService.updateRefreshToken(userDetails.getUsername(), refreshToken);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        User user = userService.getUserByUsername(username);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        userService.updateRefreshToken(username, null);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        userService.verifyUserEmail(token);
        return ResponseEntity.ok("E-mail potwierdzony! Możesz się teraz zalogować.");
    }
    
}