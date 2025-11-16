package com.peakform.meals.product.repository;

import com.peakform.meals.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByExternalApiId(String externalApiId);
}
