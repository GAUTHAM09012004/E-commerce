package com.example.cssandjavascript1.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM  Category c")
    List<Category> findAllCategories();

    Category findCategoryByCategory(String category);

    Category findCategoryById(Integer id);
}
