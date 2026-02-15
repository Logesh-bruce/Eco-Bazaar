package com.example.EcoBazaar_module2.repository;

import com.example.EcoBazaar_module2.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByVerifiedTrue();
    List<Product> findByVerifiedFalse();
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByCategory(String category);
    List<Product> findByActiveTrue();

    // Featured products
    List<Product> findByFeaturedTrueAndVerifiedTrueAndActiveTrue();
    List<Product> findByVerifiedTrueAndActiveTrue(Pageable pageable);

    /**
     * Enhanced search with multiple filters and sorting
     * Filters: name, category, price range, carbon footprint range, featured status
     * Supports sorting via Pageable
     */
    @Query("SELECT p FROM Product p WHERE " +
            "p.verified = true AND p.active = true AND " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +  // Removed LOWER()
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:featured IS NULL OR p.featured = :featured)")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("featured") Boolean featured,
            Pageable pageable
    );

    /**
     * Search products with carbon footprint filter
     * This requires joining with ProductCarbonData
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.carbonData cd WHERE " +
            "p.verified = true AND p.active = true AND " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +  // Removed LOWER()
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minCarbon IS NULL OR (cd.manufacturing + cd.transportation + cd.packaging + cd.usage + cd.disposal) >= :minCarbon) AND " +
            "(:maxCarbon IS NULL OR (cd.manufacturing + cd.transportation + cd.packaging + cd.usage + cd.disposal) <= :maxCarbon) AND " +
            "(:featured IS NULL OR p.featured = :featured)")
    Page<Product> searchProductsWithCarbonFilter(
            @Param("name") String name,
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minCarbon") Double minCarbon,
            @Param("maxCarbon") Double maxCarbon,
            @Param("featured") Boolean featured,
            Pageable pageable
    );
}