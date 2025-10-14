package com.peakform.security.user.service;

import com.peakform.claudinary.service.AvatarService;
import com.peakform.exceptions.InvalidVerificationTokenException;
import com.peakform.exceptions.UserAlreadyExistException;
import com.peakform.mailsender.MailService;
import com.peakform.pages.PagedResponse;
import com.peakform.security.auth.util.JwtUtil;
import com.peakform.security.user.dto.AuthResponse;
import com.peakform.security.user.dto.LoginRequest;
import com.peakform.security.user.dto.ProfilePhotoDTO;
import com.peakform.security.user.dto.RegisterRequest;
import com.peakform.security.user.dto.SuggestedUsersDto;
import com.peakform.security.user.dto.UserSearchDTO;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.security.user.repository.UserSpecification;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlansRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final AvatarService avatarService;
    private final WorkoutPlansRepository workoutPlanRepository;

    public void registerUser(RegisterRequest request){

        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new UserAlreadyExistException("Username is already in use");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new UserAlreadyExistException("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setEmailVerified(false);

        String avatarUrl = avatarService.generateDefaultAvatarUrl(
                request.getUsername(),
                request.getEmail()
        );

        user.setProfileImageUrl(avatarUrl);

        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);

        userRepository.save(user);

        mailService.sendVerificationEmail(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        updateRefreshToken(userDetails.getUsername(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        User user = getUserByUsername(username);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);
            return new AuthResponse(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("Invalid or expired refresh token");
        }
    }

    public void updateRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->  new UsernameNotFoundException("User not found"));
    }


    public void verifyUserEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("Invalid verification token"));

        if (user == null) {
            throw new InvalidVerificationTokenException("Nieprawidłowy token");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    public ProfilePhotoDTO getMyProfilePhoto() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new ProfilePhotoDTO(user.getProfileImageUrl());
    }

    public PagedResponse<UserSearchDTO> searchUsers(String query, Pageable pageable) {
        String currentUsername = getCurrentUsername();

        Specification<User> spec = UserSpecification.searchByUsernameExcludingCurrentUser(query, currentUsername);
        Page<User> usersPage = userRepository.findAll(spec, pageable);

        List<UserSearchDTO> dtoList = usersPage.getContent().stream()
                .map(user -> new UserSearchDTO(user.getId(), user.getUsername(), user.getProfileImageUrl()))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtoList,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @Transactional
    public void setActivePlan(Long planId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        WorkoutPlans workoutPlans = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Workout plan not found"));
        
        user.setActiveWorkoutPlan(workoutPlans);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<SuggestedUsersDto> getSuggestedUsers() {
        String username = getCurrentUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        if (currentUser.getLocation() == null || currentUser.getLocation().isEmpty()) {
            return Collections.emptyList();
        }

        List<User> suggestedUsers = userRepository.findSuggestedUsersForPostgres(
                currentUser.getLocation(),
                currentUser.getId()
        );

        return suggestedUsers.stream()
                .map(user -> new SuggestedUsersDto(user.getUsername(), user.getProfileImageUrl()))
                .collect(Collectors.toList());
    }
}