package com.peakform.meals.openfoodfacts.service;

import com.peakform.exceptions.ProfileIncompleteException;
import com.peakform.meals.openfoodfacts.dto.NutritionGoalsDto;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class NutritionGoalService {

    private final UserRepository userRepository;

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public NutritionGoalsDto calculateUserGoals(String username) {
        User user = findUserByUsername(username);

        if (user.getWeight() == null || user.getHeight() == null ||
                user.getDateOfBirth() == null || user.getGender() == null || user.getGoal() == null) {
            throw new ProfileIncompleteException("User profile data (weight, height, dob, gender, goal) is required.");
        }

        double bmr = calculateBMR(user);
        double activityFactor = getActivityFactor(user);
        double goalModifier = getGoalModifier(user);

        double tdee = bmr * activityFactor;
        int targetCalories = (int) (tdee * goalModifier);

        double targetProtein = user.getWeight() * 2.0;
        double proteinCalories = targetProtein * 4;

        double fatCalories = targetCalories * 0.25;
        double targetFat = fatCalories / 9;

        double carbsCalories = targetCalories - proteinCalories - fatCalories;
        double targetCarbs = carbsCalories / 4;

        return new NutritionGoalsDto(targetCalories, targetProtein, targetCarbs, targetFat);
    }

    // Wzorzec Mifflin-St Jeor
    private double calculateBMR(User user) {
        double weight = user.getWeight();
        double height = user.getHeight();
        int age = calculateAge(user.getDateOfBirth());

        // BMR = (10 * waga w kg) + (6.25 * wzrost w cm) - (5 * wiek w latach) + s
        double s = user.getGender().equals("MALE") ? 5 : -161;
        return (10 * weight) + (6.25 * height) - (5 * age) + s;
    }

    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new ProfileIncompleteException("Date of birth is missing.");
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private double getActivityFactor(User user) {
        if (user.getActivityLevel() == null) {
            return 1.375;
        }

        switch (user.getActivityLevel()) {
            case SEDENTARY:
                return 1.2;
            case LIGHT:
                return 1.375;
            case MODERATE:
                return 1.55;
            case ACTIVE:
                return 1.725;
            case VERY_ACTIVE:
                return 1.9;
            default:
                return 1.375;
        }
    }

    private double getGoalModifier(User user) {
        String goal = user.getGoal();
        if ("reduction".equals(goal)) {
            return 0.85; // 15% deficytu
        } else if ("bulk".equals(goal)) {
            return 1.15; // 15% nadwyżki
        } else {
            return 1.0;  // "maintenance"
        }
    }
}
