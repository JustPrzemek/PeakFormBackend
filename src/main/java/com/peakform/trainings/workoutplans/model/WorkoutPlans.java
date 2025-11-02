package com.peakform.trainings.workoutplans.model;

import com.peakform.security.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "workout_plans")
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Plan name is required")
    @Size(max = 25, message = "The plan name cannot exceed 25 characters.")
    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "The description cannot be longer than 1000 characters.")
    private String description;

    @Pattern(regexp = "reduction|bulk|maintenance")
    private String goal;

}
