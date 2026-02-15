package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Category;
import com.example.EcoBazaar_module2.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;



    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll(Sort.by("displayOrder").ascending());
    }

    @Transactional
    public Category createCategory(String name, String description, String icon) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Category already exists");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setActive(true);

        // Set display order as max + 1
        int maxOrder = categoryRepository.findAll().stream()
                .mapToInt(Category::getDisplayOrder)
                .max()
                .orElse(0);
        category.setDisplayOrder(maxOrder + 1);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String description, String icon) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }




}
