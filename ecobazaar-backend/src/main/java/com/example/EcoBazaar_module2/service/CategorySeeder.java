package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Category;
import com.example.EcoBazaar_module2.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(2) // Run after AdminUserSeeder
public class CategorySeeder implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                    new Category(null, "Clothing & Apparel", "Sustainable clothing and fashion", "fa-tshirt", true, 1),
                    new Category(null, "Home & Kitchen", "Eco-friendly home products", "fa-home", true, 2),
                    new Category(null, "Electronics", "Energy-efficient electronics", "fa-laptop", true, 3),
                    new Category(null, "Beauty & Personal Care", "Natural beauty products", "fa-spa", true, 4),
                    new Category(null, "Food & Beverages", "Organic food and drinks", "fa-utensils", true, 5),
                    new Category(null, "Books & Stationery", "Eco-friendly paper products", "fa-book", true, 6),
                    new Category(null, "Fitness & Sports", "Sustainable sports equipment", "fa-dumbbell", true, 7),
                    new Category(null, "Toys & Games", "Eco-friendly toys", "fa-gamepad", true, 8)
            );

            categoryRepository.saveAll(categories);
            System.out.println("✓ Categories seeded: " + categories.size());
        } else {
            System.out.println("✓ Categories already exist: " + categoryRepository.count());
        }
    }
}