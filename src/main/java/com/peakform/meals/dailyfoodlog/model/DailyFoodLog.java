package com.peakform.meals.dailyfoodlog.model;

import com.peakform.meals.dailyfoodlog.enums.MealType;
import com.peakform.meals.product.model.Product;
import com.peakform.meals.recipe.model.Recipe;
import com.peakform.security.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "daily_food_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyFoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @NotNull
    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private MealType mealType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.SET_NULL) // DDL: ON DELETE SET NULL
    @ToString.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @OnDelete(action = OnDeleteAction.SET_NULL) // tutaj tez mam DDL pamietacc to to znaczy ze apbo produkt albo pojedyncza rzecz moze byc w logu
    @ToString.Exclude
    private Recipe recipe;

    @Positive
    @Column(nullable = false)
    private Float quantity;

    @NotBlank
    @Column(nullable = false)
    private String unit;

    @PositiveOrZero
    @Column(name = "calories_eaten", nullable = false)
    private Float caloriesEaten;

    @PositiveOrZero
    @Column(name = "protein_eaten", nullable = false)
    private Float proteinEaten;

    @PositiveOrZero
    @Column(name = "carbs_eaten", nullable = false)
    private Float carbsEaten;

    @PositiveOrZero
    @Column(name = "fat_eaten", nullable = false)
    private Float fatEaten;
}
