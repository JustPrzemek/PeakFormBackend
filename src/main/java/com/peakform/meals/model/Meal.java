package com.peakform.meals.model;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "meals")
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String name;

    @CreationTimestamp
    @Column(name = "date", updatable = false)
    private LocalDateTime date;

    @Column(columnDefinition = "FLOAT CHECK (calories >= 0)")
    private Float calories;

    @Column(columnDefinition = "FLOAT CHECK (protein >= 0)")
    private Float protein;

    @Column(columnDefinition = "FLOAT CHECK (carbs >= 0)")
    private Float carbs;

    @Column(columnDefinition = "FLOAT CHECK (fat >= 0)")
    private Float fat;

}
