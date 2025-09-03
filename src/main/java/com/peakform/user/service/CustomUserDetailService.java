package com.peakform.user.service;

import com.peakform.user.model.User;
import com.peakform.user.model.UserPrincipal;
import com.peakform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
                //.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserPrincipal(user);
    }
}
