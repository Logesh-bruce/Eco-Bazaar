package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Category;
import com.example.EcoBazaar_module2.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories.stream()
                .map(this::toCategoryDTO)
                .toList());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> request) {
        try {
            String name = request.get("name").toString();
            String description = request.getOrDefault("description", "").toString();
            String icon = request.getOrDefault("icon", "fa-box").toString();

            Category category = categoryService.createCategory(name, description, icon);
            return ResponseEntity.ok(toCategoryDTO(category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String name = request.get("name").toString();
            String description = request.getOrDefault("description", "").toString();
            String icon = request.getOrDefault("icon", "fa-box").toString();

            Category category = categoryService.updateCategory(id, name, description, icon);
            return ResponseEntity.ok(toCategoryDTO(category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("message", "Category deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toCategoryDTO(Category category) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", category.getId());
        dto.put("name", category.getName());
        dto.put("description", category.getDescription());
        dto.put("icon", category.getIcon());
        dto.put("active", category.isActive());
        dto.put("displayOrder", category.getDisplayOrder());
        return dto;
    }
}
