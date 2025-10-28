package com.peakform.security.user.model;

import com.peakform.comments.model.Comments;
import com.peakform.followers.model.Followers;
import com.peakform.meals.model.Meal;
import com.peakform.postlikes.model.PostLikes;
import com.peakform.posts.model.Post;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_bio")
    private String profileBio;

    @Column(name = "bio_title")
    private String bioTitle;

    @Column(name = "location")
    private String location;

    @Pattern(regexp = "MALE|FEMALE")
    private String gender;

    @Past(message = "Birth date must be from the past.")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "follower")
    @ToString.Exclude
    private List<Followers> following;  // Kogo obserwuje

    @OneToMany(mappedBy = "followed")
    @ToString.Exclude
    private List<Followers> followers;  // Kto go obserwuje

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Comments> comments;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Meal> meals;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<PostLikes> likes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_workout_plan_id")
    @ToString.Exclude
    private WorkoutPlans activeWorkoutPlan;
}