package com.peakform.security.auth.service;

import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserProcessor {

    private final UserRepository userRepository;

    public User processOAuth2User(String providerId, String email) {

        Optional<User> userOptional = userRepository.findByProviderId(providerId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        User existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail != null) {
            // Można połączyć konto lub rzucić błąd
            throw new RuntimeException("User with this email already exists. Please log in with your password.");
        }

        User newUser = new User();
        newUser.setAuthProvider("google");
        newUser.setProviderId(providerId);
        newUser.setEmail(email);
        newUser.setEmailVerified(true);
        newUser.setEnabled(true);

        // unikalna nazwa użytkownika
        String baseUsername = email.split("@")[0];
        String finalUsername = baseUsername;
        int counter = 1;
        while (userRepository.findByUsername(finalUsername) != null) {
            finalUsername = baseUsername + counter;
            counter++;
        }
        newUser.setUsername(finalUsername);

        newUser.setPasswordHash(null);

        return userRepository.save(newUser);
    }
}
