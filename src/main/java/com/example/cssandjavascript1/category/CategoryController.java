package com.example.cssandjavascript1.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Operation(
            description = "Get Endpoint for Categories",
            summary = "returns a list of all categories"
    )
    @GetMapping
    public List<Category> findallcategories() {
        return categoryRepository.findAllCategories();
    }

    @Operation(
            description = "Get Endpoint for Category",
            summary = "returns category of given id from all"
    )
    @GetMapping("/{id}")
    public Optional<Category> findcategorybyid(@PathVariable Integer id) {
        return categoryRepository.findById(id);
    }

    @Operation(
            description = "Delete Endpoint for Category",
            summary = "deletes category of given id from all"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> categorybyid(@PathVariable Integer id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted");
    }

    @Operation(
            description = "Post Endpoint for Category",
            summary = "creates a new category"
    )
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

    @Operation(
            description = "Put Endpoint for Category",
            summary = "updates a category"
    )
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
