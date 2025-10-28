package com.peakform.security.auth.service;

import com.peakform.claudinary.service.AvatarService;
import com.peakform.security.user.model.User;
import com.peakform.security.user.model.UserPrincipal;
import com.peakform.security.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AvatarService avatarService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // Szukamy użytkownika po emailu
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            // UŻYTKOWNIK ISTNIEJE
            user = userOptional.get();

            // Sprawdzamy, czy użytkownik pierwotnie zarejestrował się przez inny serwis (np. lokalnie)
            if (!user.getAuthProvider().equalsIgnoreCase(provider)) {
                // To jest przypadek, gdy ktoś miał konto lokalne i teraz łączy je z Google.
                // Aktualizujemy jego dane o dostawcę OAuth2.
                user.setAuthProvider(provider);
                user.setProviderId(oAuth2User.getName());
                user.setProfileImageUrl(oAuth2User.getAttribute("picture"));
            }
            // Jeśli użytkownik już istnieje i logował się wcześniej przez Google,
            // to po prostu go zwracamy, nie ma potrzeby nic zmieniać.

        } else {
            // NOWY UŻYTKOWNIK
            user = new User();
            user.setEmail(email);
            user.setProfileImageUrl(oAuth2User.getAttribute("picture"));
            user.setAuthProvider(provider);
            user.setProviderId(oAuth2User.getName());
            user.setEnabled(true);
            user.setEmailVerified(true); // Email z Google jest z definicji zweryfikowany

            String baseUsername = oAuth2User.getAttribute("name");
            if (baseUsername == null || baseUsername.isBlank()) {
                baseUsername = email.split("@")[0];
            }
            String finalUsername = baseUsername;
            int counter = 1;
            // Pętla gwarantuje znalezienie unikalnej nazwy
            while (userRepository.findByUsername(finalUsername).isPresent()) {
                finalUsername = baseUsername + counter;
                counter++;
            }
            user.setUsername(finalUsername);

            if (user.getProfileImageUrl() == null) {
                user.setProfileImageUrl(avatarService.generateDefaultAvatarUrl(user.getUsername(), user.getEmail()));
            }
        }

        userRepository.save(user); // Zapisujemy zmiany lub nowego użytkownika
        return new UserPrincipal(user, oAuth2User.getAttributes());
    }
}

