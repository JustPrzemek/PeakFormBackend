package com.peakform.userprofile.service;

import com.peakform.claudinary.service.ImageUploadService;
import com.peakform.exceptions.UserAlreadyExistException;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.userprofile.dto.EditUserDataDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import com.peakform.userprofile.mapper.UserProfileMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    private final ImageUploadService imageUploadService;


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
        if (dto.getProfileBio() != null) {
            user.setProfileBio(dto.getProfileBio());
        }
        if (dto.getLocation() != null) {
            user.setLocation(dto.getLocation());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
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
}
