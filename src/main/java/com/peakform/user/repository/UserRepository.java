package com.peakform.user.repository;

import com.peakform.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);
}
