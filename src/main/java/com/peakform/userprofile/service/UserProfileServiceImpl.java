package com.peakform.userprofile.service;

import com.peakform.claudinary.service.ImageUploadService;
import com.peakform.exceptions.AlreadyFollowingException;
import com.peakform.exceptions.FileTooLargeException;
import com.peakform.exceptions.FollowNotFoundException;
import com.peakform.exceptions.UserAlreadyExistException;
import com.peakform.followers.model.Followers;
import com.peakform.followers.repository.FollowersRepository;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.userprofile.dto.EditUserDataDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import com.peakform.userprofile.mapper.UserProfileMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    private final ImageUploadService imageUploadService;
    private final FollowersRepository followersRepository;


    @Override
    public UserProfileDTO getUserProfile(String username){
        return userRepository.findUserProfileDtoByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public UserProfileDTO getUserMe(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserProfileDtoByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    @Transactional
    public EditUserDataDTO updateUserData(EditUserDataDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (dto.getUsername() != null && !Objects.equals(username, dto.getUsername())) {
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new UserAlreadyExistException("Username " + dto.getUsername() + " is already taken.");
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getBioTitle() != null) {
            user.setBioTitle(dto.getBioTitle());
        }
        if (dto.getProfileBio() != null) {
            user.setProfileBio(dto.getProfileBio());
        }
        if (dto.getLocation() != null) {
            user.setLocation(dto.getLocation());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getWeight() != null) {
            user.setWeight(dto.getWeight());
        }
        if (dto.getHeight() != null) {
            user.setHeight(dto.getHeight());
        }
        if (dto.getGoal() != null) {
            user.setGoal(dto.getGoal());
        }

        userRepository.save(user);
        return userProfileMapper.userToEditUserDataDTO(user);
    }

    @Override
    public String updateProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        long maxSizeInBytes = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSizeInBytes) {
            throw new FileTooLargeException("Profile image size exceeds the limit of 5 MB");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldImageUrl = user.getProfileImageUrl();
        if(oldImageUrl != null && !oldImageUrl.isEmpty()){
            imageUploadService.deleteImage(oldImageUrl);
        }

        String imageUrl = imageUploadService.uploadImage(file);
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    @Override
    public EditUserDataDTO getEditedUserData() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userProfileMapper.userToEditUserDataDTO(user);
    }

    @Override
    public void updateFollowers(String username) {

        String usernameMe = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(usernameMe)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User followedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.equals(followedUser)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        boolean exists = followersRepository.existsByFollowerAndFollowed(user, followedUser);
        if (exists) {
            throw new AlreadyFollowingException("User already follows " + username);
        }

        Followers followers = new Followers();
        followers.setFollower(user);
        followers.setFollowed(followedUser);

        followersRepository.save(followers);
    }

    @Override
    @Transactional
    public void unfollowUser(String username) {

        String usernameMe = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(usernameMe)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User followedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.equals(followedUser)) {
            throw new IllegalArgumentException("You cannot unfollow yourself");
        }

        boolean exists = followersRepository.existsByFollowerAndFollowed(currentUser, followedUser);
        if (!exists) {
            throw new FollowNotFoundException("User do not follow" + username);
        }

        followersRepository.deleteByFollowerAndFollowed(currentUser, followedUser);
    }



}
