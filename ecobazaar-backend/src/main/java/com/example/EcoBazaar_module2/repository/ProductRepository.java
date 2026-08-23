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
    List<Product> findByStatusIgnoreCase(String status);
    List<Product> findByStatusInIgnoreCase(List<String> statuses);
    List<Product> findBySellerId(Long sellerId);
    List<Product> findBySeller_Id(Long sellerId);
    List<Product> findByCategory(String category);
    List<Product> findByActiveTrue();

    // Featured products
    List<Product> findByFeaturedTrueAndVerifiedTrueAndActiveTrue();
    List<Product> findByVerifiedTrueAndActiveTrue(Pageable pageable);

    // Pending products query handling status and verified flags
    @Query("SELECT p FROM Product p WHERE p.verified = false OR p.verified IS NULL OR UPPER(p.status) = 'PENDING' OR p.status IS NULL")
    List<Product> findPendingProducts();

    // Approved products query
    @Query("SELECT p FROM Product p WHERE p.verified = true OR UPPER(p.status) = 'APPROVED'")
    List<Product> findApprovedProducts();

    // Shop products: all products that should be visible in the storefront
    @Query("SELECT p FROM Product p WHERE p.verified = true OR LOWER(p.status) IN ('approved', 'active', 'verified', 'pending') OR p.status IS NULL")
    List<Product> findShopProducts();

    /**
     * Enhanced search with multiple filters and sorting
     * Filters: name, category, price range, carbon footprint range, featured status
     * Supports sorting via Pageable
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(p.verified = true OR LOWER(p.status) IN ('approved', 'active', 'verified', 'pending') OR p.status IS NULL) AND " +
            "(p.active = true OR p.active IS NULL) AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
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
     * Search products with carbon footprint filter using COALESCE for null-safety
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.carbonData cd WHERE " +
            "(p.verified = true OR LOWER(p.status) IN ('approved', 'active', 'verified', 'pending') OR p.status IS NULL) AND " +
            "(p.active = true OR p.active IS NULL) AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minCarbon IS NULL OR (COALESCE(cd.manufacturing, 0.0) + COALESCE(cd.transportation, 0.0) + COALESCE(cd.packaging, 0.0) + COALESCE(cd.usage, 0.0) + COALESCE(cd.disposal, 0.0)) >= :minCarbon) AND " +
            "(:maxCarbon IS NULL OR (COALESCE(cd.manufacturing, 0.0) + COALESCE(cd.transportation, 0.0) + COALESCE(cd.packaging, 0.0) + COALESCE(cd.usage, 0.0) + COALESCE(cd.disposal, 0.0)) <= :maxCarbon) AND " +
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