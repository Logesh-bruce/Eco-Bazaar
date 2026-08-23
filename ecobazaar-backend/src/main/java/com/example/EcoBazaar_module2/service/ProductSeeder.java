package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Product;
import com.example.EcoBazaar_module2.model.ProductCarbonData;
import com.example.EcoBazaar_module2.model.Role;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.repository.ProductCarbonDataRepository;
import com.example.EcoBazaar_module2.repository.ProductRepository;
import com.example.EcoBazaar_module2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Seeds initial eco-friendly products into the database if empty.
 * Runs with @Order(3) after AdminUserSeeder and CategorySeeder.
 */
@Component
@Order(3)
public class ProductSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCarbonDataRepository carbonDataRepository;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            System.out.println("=====================================");
            System.out.println("🌿 Seeding initial Eco-Bazaar products...");

            // 1. Get or create a default seller/admin user
            User seller = userRepository.findByRole(Role.ADMIN).stream().findFirst().orElse(null);
            if (seller == null) {
                seller = userRepository.findByEmail("admin@ecobazaar.com").orElse(null);
            }
            if (seller == null) {
                seller = new User();
                seller.setEmail("seller@ecobazaar.com");
                seller.setPassword(passwordEncoder.encode("seller123"));
                seller.setFullName("Eco Marketplace Seller");
                seller.setRole(Role.SELLER);
                seller.setActive(true);
                seller = userRepository.save(seller);
            }

            // 2. Seed default approved products
            createSeedProduct(
                    seller,
                    "Bamboo Cutlery Set",
                    "Handcrafted zero-waste bamboo cutlery set with travel pouch. Includes spoon, fork, knife, chopsticks, straw, and cleaning brush. 100% biodegradable and sustainably harvested.",
                    19.99,
                    50,
                    "Home & Kitchen",
                    "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=600&q=80",
                    "APPROVED",
                    true,
                    true,
                    0.3, 0.2, 0.1, 0.0, 0.1 // 0.7 kg CO2e
            );

            createSeedProduct(
                    seller,
                    "Organic Cotton Tote Bag",
                    "100% GOTS certified organic cotton reusable shopping tote bag with reinforced handles. Sturdy, washable, and replaces hundreds of single-use plastic bags.",
                    14.50,
                    100,
                    "Clothing & Apparel",
                    "https://images.unsplash.com/photo-1597484661643-2f5fef640dd1?auto=format&fit=crop&w=600&q=80",
                    "APPROVED",
                    true,
                    true,
                    0.4, 0.2, 0.1, 0.0, 0.1 // 0.8 kg CO2e
            );

            createSeedProduct(
                    seller,
                    "Reusable Stainless Steel Water Bottle",
                    "Vacuum-insulated double-wall 750ml stainless steel bottle. Keeps cold drinks iced for 24h and hot drinks steaming for 12h. BPA-free and leakproof.",
                    28.00,
                    45,
                    "Home & Kitchen",
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=600&q=80",
                    "APPROVED",
                    true,
                    true,
                    0.8, 0.3, 0.1, 0.0, 0.1 // 1.3 kg CO2e
            );

            createSeedProduct(
                    seller,
                    "Solar Powered Power Bank",
                    "High-efficiency 20,000mAh portable charger with solar charging panels, dual USB-C output, and rugged shockproof casing for outdoor adventures.",
                    49.99,
                    30,
                    "Electronics",
                    "https://images.unsplash.com/photo-1620714223084-8fcacc6dfd8d?auto=format&fit=crop&w=600&q=80",
                    "APPROVED",
                    true,
                    true,
                    2.0, 0.5, 0.2, 0.0, 0.2 // 2.9 kg CO2e
            );

            createSeedProduct(
                    seller,
                    "Natural Beeswax Food Wraps (Pack of 3)",
                    "Sustainable, washable alternative to plastic cling wrap made from GOTS organic cotton, sustainably harvested beeswax, jojoba oil, and tree resin.",
                    16.99,
                    60,
                    "Home & Kitchen",
                    "https://images.unsplash.com/photo-1610557892470-55d9e80c0bce?auto=format&fit=crop&w=600&q=80",
                    "APPROVED",
                    true,
                    false,
                    0.2, 0.1, 0.1, 0.0, 0.0 // 0.4 kg CO2e
            );

            createSeedProduct(
                    seller,
                    "Zero Waste Organic Shampoo Bar",
                    "Plastic-free, 100% organic plant-based hair care bar packed with argan oil, shea butter, and natural peppermint essential oils. Equivalent to 3 bottles of liquid shampoo.",
                    11.99,
                    80,
                    "Beauty & Personal Care",
                    "https://images.unsplash.com/photo-1608248597359-242548f0e572?auto=format&fit=crop&w=600&q=80",
                    "APPROVED",
                    true,
                    false,
                    0.2, 0.1, 0.05, 0.0, 0.05 // 0.4 kg CO2e
            );

            // 3. Seed 1 pending product to showcase the Admin verification approval flow
            createSeedProduct(
                    seller,
                    "Handcrafted Coconut Shell Bowl",
                    "Handmade eco-friendly smoothie bowl crafted from 100% reclaimed natural coconut shells with organic coconut oil polish.",
                    12.50,
                    35,
                    "Home & Kitchen",
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=600&q=80",
                    "PENDING",
                    false,
                    false,
                    0.1, 0.2, 0.05, 0.0, 0.05 // 0.4 kg CO2e
            );

            System.out.println("✓ Product seeding complete! (" + productRepository.count() + " products created)");
            System.out.println("=====================================");
        } else {
            System.out.println("✓ Products already exist in database: " + productRepository.count());
        }
    }

    private void createSeedProduct(
            User seller,
            String name,
            String description,
            Double price,
            Integer quantity,
            String category,
            String imageBase64,
            String status,
            boolean verified,
            boolean featured,
            Double mfg, Double trans, Double pack, Double usage, Double disp
    ) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setImageBase64(imageBase64);
        product.setSeller(seller);
        product.setActive(true);
        product.setVerified(verified);
        product.setStatus(status);
        product.setFeatured(featured);
        product.setViewCount(5);
        product.setSoldCount(2);
        product.setAverageRating(4.8);
        product.setReviewCount(1);

        Product savedProduct = productRepository.save(product);

        ProductCarbonData carbonData = new ProductCarbonData();
        carbonData.setProduct(savedProduct);
        carbonData.setManufacturing(mfg != null ? mfg : 0.0);
        carbonData.setTransportation(trans != null ? trans : 0.0);
        carbonData.setPackaging(pack != null ? pack : 0.0);
        carbonData.setUsage(usage != null ? usage : 0.0);
        carbonData.setDisposal(disp != null ? disp : 0.0);

        carbonDataRepository.save(carbonData);
    }
}
