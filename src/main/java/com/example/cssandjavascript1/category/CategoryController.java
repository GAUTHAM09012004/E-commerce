package com.example.cssandjavascript1.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> findallcategories() {
        return categoryRepository.findAllCategories();
    }

    @GetMapping("/{id}")
    public Optional<Category> findcategorybyid(@PathVariable Integer id) {
        return categoryRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> categorybyid(@PathVariable Integer id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted");
    }

    @PostMapping
    public ResponseEntity<String> createcategory(@RequestBody HashMap<String, String> map) {
        if (!map.containsKey("category")) {
            return ResponseEntity.badRequest().body("Enter correct data");
        }
        Category category = Category.builder()
                .category(map.get("category"))
                .build();
        categoryRepository.save(category);
        return ResponseEntity.ok().body("Category created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatecategorybyid(@PathVariable Integer id, @RequestBody HashMap<String, String> map) {
        if (!map.containsKey("category")) {
            return ResponseEntity.badRequest().body("Enter correct data");
        }
        Category category = categoryRepository.findCategoryById(id);
        category.setCategory(map.get("category"));
        categoryRepository.save(category);
        return ResponseEntity.ok().body("Category updated successfully");
    }
}
