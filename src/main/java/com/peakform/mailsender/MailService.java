package com.peakform.mailsender;

import com.peakform.security.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${verification.email.url}")
    private String verificationUri;

    @Value("${resetpassword.url}")
    private String resetPasswordUri;

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendVerificationEmail(User user) {
        String subject = "Verification Email";
        String verificationUrl = verificationUri + user.getEmailVerificationToken();

        String text = "Hiiii :D " + user.getUsername() + ",\n\n"
                + "Click the link below to confirm your email address:\n"
                + verificationUrl + "\n\n"
                + "Regards,\nTeam PeakForm";

        sendMail(user.getEmail(), subject, text);
    }

    public void sendPasswordResetEmail(User user, String rawToken) {
        String subject = "Password reset";
        String resetUrl = resetPasswordUri + rawToken;

        String text = "Hiiii :D" + user.getUsername() + ",\n\n"
                + "Click the link below to reset your password:\n"
                + resetUrl + "\n\n"
                + "If you did not request a reset, simply ignore this message.\n\n"
                + "Regards,\nTeam PeakForm";

        sendMail(user.getEmail(), subject, text);
    }
}