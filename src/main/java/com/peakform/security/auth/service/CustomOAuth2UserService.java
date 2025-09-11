package com.peakform.security.auth.service;

import com.peakform.security.user.model.User;
import com.peakform.security.user.model.UserPrincipal;
import com.peakform.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserProcessor oAuth2UserProcessor;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String providerId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");

        User user = oAuth2UserProcessor.processOAuth2User(providerId, email);

        return new UserPrincipal(user, oauth2User.getAttributes());
    }
}
