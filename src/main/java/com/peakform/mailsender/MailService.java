package com.peakform.mailsender;

import com.peakform.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendVerificationEmail(User user) {
        String subject = "Verification Email";
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + user.getEmailVerificationToken();

        String text = "Cześć " + user.getUsername() + ",\n\n"
                + "Kliknij w poniższy link, aby potwierdzić swój adres e-mail:\n"
                + verificationUrl + "\n\n"
                + "Pozdrawiam,\nZespół PeakForm";

        sendMail(user.getEmail(), subject, text);
    }

    public void sendPasswordResetEmail(User user, String rawToken) {
        String subject = "Reset hasła";
        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + rawToken;

        String text = "Cześć " + user.getUsername() + ",\n\n"
                + "Kliknij w poniższy link, aby zresetować swoje hasło:\n"
                + resetUrl + "\n\n"
                + "Jeśli to nie Ty prosiłeś o reset, po prostu zignoruj tę wiadomość.\n\n"
                + "Pozdrawiam,\nZespół PeakForm";

        sendMail(user.getEmail(), subject, text);
    }
}