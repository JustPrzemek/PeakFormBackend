package com.peakform.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String username;

    @Positive
    private Integer age;

    @Positive
    private Float weight;

    @Positive
    private Float height;

    @Pattern(regexp = "reduction|bulk|maintenance")
    private String goal;

    @Column(name = "auth_provider")
    private String authProvider = "local";

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "role", nullable = false)
    private String role = "USER";

    @Column(name = "is_enabled")
    private boolean isEnabled = true;

    @Column(name = "created_at")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    @Column(name = "last_password_reset_request")
    private LocalDateTime lastPasswordResetRequest;

    @Column(name = "reset_attempts_today")
    private int resetAttemptsToday;

    @Column(name = "reset_attempts_date")
    private LocalDate resetAttemptsDate;
}