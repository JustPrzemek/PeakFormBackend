package com.peakform.meals.dailyfoodlog.service;

import com.peakform.exceptions.LogAccessDeniedException;
import com.peakform.exceptions.LogNotFoundException;
import com.peakform.meals.dailyfoodlog.dto.AddFoodLogDto;
import com.peakform.meals.dailyfoodlog.dto.DailyLogDto;
import com.peakform.meals.dailyfoodlog.dto.FoodLogEntryDto;
import com.peakform.meals.dailyfoodlog.dto.NutritionSummaryDto;
import com.peakform.meals.dailyfoodlog.enums.MealType;
import com.peakform.meals.dailyfoodlog.model.DailyFoodLog;
import com.peakform.meals.dailyfoodlog.repository.DailyFoodLogRepository;
import com.peakform.meals.openfoodfacts.dto.OffProductDto;
import com.peakform.meals.openfoodfacts.service.OpenFoodFactsService;
import com.peakform.meals.product.model.Product;
import com.peakform.meals.product.repository.ProductRepository;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyFoodLogService {

    private final DailyFoodLogRepository dailyFoodLogRepository;
    private final ProductRepository productRepository;
    private final OpenFoodFactsService openFoodFactsService;
    private final UserRepository userRepository;

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public void logFood(AddFoodLogDto dto, String username) {
        User user = findUserByUsername(username);

        Product product = findOrCacheProduct(dto.getExternalApiId());

        double quantity = dto.getQuantity();
        Float calories = (float) ((product.getCaloriesPer100g() / 100.0) * quantity);
        Float protein = (float) ((product.getProteinPer100g() / 100.0) * quantity);
        Float carbs = (float) ((product.getCarbsPer100g() / 100.0) * quantity);
        Float fat = (float) ((product.getFatPer100g() / 100.0) * quantity);

        DailyFoodLog log = new DailyFoodLog();
        log.setUser(user);
        log.setProduct(product);
        log.setDate(dto.getDate());
        log.setMealType(dto.getMealType());
        log.setQuantity((float) quantity);
        log.setUnit(product.getDefaultUnit());
        log.setCaloriesEaten(calories);
        log.setProteinEaten(protein);
        log.setCarbsEaten(carbs);
        log.setFatEaten(fat);

        dailyFoodLogRepository.save(log);
    }

    private Product findOrCacheProduct(String externalApiId) {
        Optional<Product> existingProduct = productRepository.findByExternalApiId(externalApiId);
        if (existingProduct.isPresent()) {
            return existingProduct.get();
        }

        OffProductDto offProduct = openFoodFactsService.getFullProductById(externalApiId);
        if (offProduct.getNutriments() == null) {
            throw new RuntimeException("Product from API has no nutrition data.");
        }

        Product newProduct = new Product();
        newProduct.setExternalApiId(offProduct.getExternalApiId());
        newProduct.setName(Optional.ofNullable(offProduct.getName()).orElse("Unknown Product"));
        newProduct.setBrand(Optional.ofNullable(offProduct.getBrand()).orElse("Unknown Brand"));
        newProduct.setDefaultUnit(Optional.ofNullable(offProduct.getUnit()).orElse("g"));
        newProduct.setCaloriesPer100g(Optional.ofNullable(offProduct.getNutriments().getCaloriesPer100g()).map(Double::floatValue).orElse(0.0f));
        newProduct.setProteinPer100g(Optional.ofNullable(offProduct.getNutriments().getProteinPer100g()).map(Double::floatValue).orElse(0.0f));
        newProduct.setCarbsPer100g(Optional.ofNullable(offProduct.getNutriments().getCarbsPer100g()).map(Double::floatValue).orElse(0.0f));
        newProduct.setFatPer100g(Optional.ofNullable(offProduct.getNutriments().getFatPer100g()).map(Double::floatValue).orElse(0.0f));
        return productRepository.save(newProduct);
    }

    @Transactional(readOnly = true)
    public DailyLogDto getDailyLog(LocalDate date, String username) {
        User user = findUserByUsername(username);

        List<DailyFoodLog> logs = dailyFoodLogRepository.findByUserIdAndDate(user.getId(), date);

        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;
        Map<MealType, List<FoodLogEntryDto>> meals = new EnumMap<>(MealType.class);
        for (MealType type : MealType.values()) {
            meals.put(type, new ArrayList<>());
        }

        for (DailyFoodLog log : logs) {
            FoodLogEntryDto entryDto = mapToLogEntryDto(log);
            meals.get(log.getMealType()).add(entryDto);

            totalCalories += log.getCaloriesEaten();
            totalProtein += log.getProteinEaten();
            totalCarbs += log.getCarbsEaten();
            totalFat += log.getFatEaten();
        }

        NutritionSummaryDto summary = new NutritionSummaryDto(totalCalories, totalProtein, totalCarbs, totalFat);
        return new DailyLogDto(summary, meals);
    }

    @Transactional
    public void deleteFoodLog(Long logEntryId, String username) {
        User user = findUserByUsername(username);

        DailyFoodLog log = dailyFoodLogRepository.findById(logEntryId)
                .orElseThrow(() -> new LogNotFoundException("Food log entry not found with id: " + logEntryId));

        if (!log.getUser().getId().equals(user.getId())) {
            throw new LogAccessDeniedException("You do not have permission to delete this log entry.");
        }

        dailyFoodLogRepository.delete(log);
    }

    private FoodLogEntryDto mapToLogEntryDto(DailyFoodLog log) {
        Product p = log.getProduct();

        String productName = (p != null) ? p.getName() : "Deleted Product";
        String productBrand = (p != null) ? p.getBrand() : "N/A";

        return new FoodLogEntryDto(
                log.getId(),
                productName,
                productBrand,
                log.getQuantity(),
                log.getUnit(),
                log.getCaloriesEaten(),
                log.getProteinEaten(),
                log.getCarbsEaten(),
                log.getFatEaten()
        );
    }
}
