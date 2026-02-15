package com.example.EcoBazaar_module2.repository;

import com.example.EcoBazaar_module2.model.ProductCarbonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCarbonDataRepository extends JpaRepository<ProductCarbonData, Long> {
    Optional<ProductCarbonData> findByProductId(Long productId);
}
