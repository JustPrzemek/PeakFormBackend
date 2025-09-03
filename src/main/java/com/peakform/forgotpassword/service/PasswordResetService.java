package com.peakform.forgotpassword.service;

import com.peakform.exceptions.TooManyRequestsException;
import com.peakform.forgotpassword.dto.ForgotPasswordRequest;
import com.peakform.forgotpassword.dto.ResetPasswordRequest;
import com.peakform.mailsender.MailService;
import com.peakform.user.model.User;
import com.peakform.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return; // nie ujawniamy czy email istnieje
        }

        LocalDateTime now = LocalDateTime.now();

        // reset liczby prób jeśli zmienił się dzień
        if (user.getResetAttemptsDate() == null || !user.getResetAttemptsDate().isEqual(now.toLocalDate())) {
            user.setResetAttemptsDate(now.toLocalDate());
            user.setResetAttemptsToday(0);
        }

        // sprawdzenie ostatniego czasu
        if (user.getLastPasswordResetRequest() != null) {
            LocalDateTime earliestNext = user.getLastPasswordResetRequest().plusMinutes(10);
            if (earliestNext.isAfter(now)) {
                long minutesLeft = Duration.between(now, earliestNext).toMinutes();
                throw new TooManyRequestsException("Spróbuj ponownie za " + minutesLeft + " minut.");
            }
        }

        // sprawdzenie limitu dziennego
        if (user.getResetAttemptsToday() >= 5) {
            throw new TooManyRequestsException("Limit resetów hasła został wyczerpany. Spróbuj jutro.");
        }

        // generowanie losowego tokenu
        String rawToken = generateSecureToken();
        String hashedToken = hashToken(rawToken);

        user.setPasswordResetToken(hashedToken);
        user.setPasswordResetExpires(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        // wysłanie maila z linkiem
        mailService.sendPasswordResetEmail(user, rawToken);

    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String hashedToken = hashToken(request.getToken());

        Optional<User> userOpt = userRepository.findByPasswordResetToken(hashedToken);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Nieprawidłowy token");
        }

        User user = userOpt.get();

        if (user.getPasswordResetExpires() == null || user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token wygasł");
        }

        // walidacja nowego hasła (tu np. minimalna długość)
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Hasło musi mieć min. 8 znaków");
        }

        // ustaw nowe hasło
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // wyczyść token i refreshToken
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        user.setRefreshToken(null);

        userRepository.save(user);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Błąd podczas hashowania tokenu", e);
        }
    }

}
