package com.peakform.security.user.repository;

import com.peakform.security.user.model.User;
import com.peakform.userprofile.dto.UserProfileDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    Optional<User> findByProviderId(String providerId);

    @Query(value = """
    SELECT u.username, u.profile_image_url, u.profile_bio, u.bio_title, u.location,
           (SELECT COUNT(*) FROM followers f WHERE f.followed_id = u.id) as followers_count,
           (SELECT COUNT(*) FROM followers f WHERE f.follower_id = u.id) as following_count,
           (SELECT COUNT(*) FROM posts p WHERE p.user_id = u.id) as posts_count,
           EXISTS (SELECT 1 FROM followers f WHERE f.followed_id = u.id AND f.follower_id = :currentUserId) as is_following
    FROM users u
    WHERE u.username = :username
    """, nativeQuery = true)
    Optional<UserProfileDTO> findUserProfileDtoByUsername(
            @Param("username") String username,
            @Param("currentUserId") Long currentUserId
    );

    @Query(value = "SELECT * FROM users u WHERE u.location = :location " +
            "AND u.id != :currentUserId " +
            "AND u.id NOT IN (SELECT f.followed_id FROM followers f WHERE f.follower_id = :currentUserId) " +
            "ORDER BY RANDOM() LIMIT 8", nativeQuery = true)
    List<User> findSuggestedUsersForPostgres(@Param("location") String location, @Param("currentUserId") Long currentUserId);
}
