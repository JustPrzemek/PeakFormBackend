package com.peakform.user.service;

import com.peakform.exceptions.InvalidVerificationTokenException;
import com.peakform.mailsender.MailService;
import com.peakform.user.dto.RegisterRequest;
import com.peakform.user.model.User;
import com.peakform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public void registerUser(RegisterRequest request){

        if (userRepository.findByUsername(request.getUsername()) != null){
            throw new RuntimeException("Username is already in use");
        }
        if (userRepository.findByEmail(request.getEmail()) != null){
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);

        userRepository.save(user);

        mailService.sendVerificationEmail(user);
    }

    public void updateRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public void verifyUserEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token);

        if (user == null) {
            throw new InvalidVerificationTokenException("Nieprawidłowy token");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

}