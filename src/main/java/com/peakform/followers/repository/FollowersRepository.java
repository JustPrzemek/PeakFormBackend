package com.peakform.followers.repository;

import com.peakform.followers.model.Followers;
import com.peakform.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, Long> {

    boolean existsByFollowerAndFollowed(User follower, User followed);

    void deleteByFollowerAndFollowed(User follower, User followedUser);

    Page<Followers> findByFollowed(User followed, Pageable pageable);

    Page<Followers> findByFollower(User follower, Pageable pageable);

    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    Page<Followers> findByFollowedAndFollower(User followed, User followedUsername, Pageable pageable);

    Optional<Followers> findByFollowerAndFollowed(User follower, User followed);

    long countByFollowed(User userToFollow);
}
