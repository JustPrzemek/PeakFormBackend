package com.peakform.meals.product.model;

import com.peakform.security.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String barcode;

    @Column(name = "external_api_id")
    private String externalApiId;

    @Column(name = "brand")
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ToString.Exclude
    private User user;

    @PositiveOrZero
    @Column(name = "calories_per_100g", nullable = false)
    private Float caloriesPer100g;

    @PositiveOrZero
    @Column(name = "protein_per_100g", nullable = false)
    private Float proteinPer100g;

    @PositiveOrZero
    @Column(name = "carbs_per_100g", nullable = false)
    private Float carbsPer100g;

    @PositiveOrZero
    @Column(name = "fat_per_100g", nullable = false)
    private Float fatPer100g;

    @NotBlank
    @Column(name = "default_unit", nullable = false)
    @ColumnDefault("'g'")
    private String defaultUnit = "g";
}
