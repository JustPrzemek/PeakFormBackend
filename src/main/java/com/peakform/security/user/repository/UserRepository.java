package com.peakform.security.user.repository;

import com.peakform.security.user.model.User;
import com.peakform.userprofile.dto.UserProfileDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    User findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    Optional<User> findByProviderId(String providerId);

    @Query(value = """
    SELECT u.username, u.profile_image_url, u.profile_bio, u.location,
           (SELECT COUNT(*) FROM followers f WHERE f.followed_id = u.id) as followers_count,
           (SELECT COUNT(*) FROM followers f WHERE f.follower_id = u.id) as following_count,
           (SELECT COUNT(*) FROM posts p WHERE p.user_id = u.id) as posts_count
    FROM users u
    WHERE u.username = :username
    """, nativeQuery = true)
    Optional<UserProfileDTO> findUserProfileDtoByUsername(@Param("username") String username);
}
