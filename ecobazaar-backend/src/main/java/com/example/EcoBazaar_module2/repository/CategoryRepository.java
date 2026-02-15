package com.example.EcoBazaar_module2.repository;

import com.example.EcoBazaar_module2.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();

    Optional<Category> findByName(String name);
}
