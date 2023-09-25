package com.example.cssandjavascript1.Controller;

import com.example.cssandjavascript1.Models.CategoryRepository;
import com.example.cssandjavascript1.Models.Product;
import com.example.cssandjavascript1.auth.ProductRequest;
import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.user.UseRepository;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.cssandjavascript1.Models.ProductRespository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRespository productRespository;
    private final CategoryRepository categoryRespository;

    @GetMapping
    public List<Product> findallproducts() {
        return productRespository.findAllProducts();
    }

    @GetMapping("/{id}")
    public Optional<Product> findproductsbyid(@PathVariable Integer id) {
        return productRespository.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> productsbyid(@PathVariable Integer id) {
        productRespository.deleteById(id);
        return ResponseEntity.ok("Product deleted");
    }

    @PostMapping()
    public ResponseEntity<String> createProduct(@RequestBody ProductRequest product) {
        Product product1 = Product.builder()
                .product_name(product.product_name())
                .stock(product.stock())
                .price(product.price())
                .image(product.image())
                .date(product.expected_date_of_arrival())
                .rating(product.rating())
                .category(categoryRespository.findCategoryByCategory(product.categoryname()))
                .build();
        productRespository.save(product1);
        return ResponseEntity.ok("Product created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modifyproductybyid(@RequestBody ProductRequest product, @PathVariable Integer id) {
        try {
            Optional<Product> optionalProduct = productRespository.findById(id);
            if (optionalProduct.isPresent()) {
                Product productToUpdate = optionalProduct.get();
                productToUpdate.setProduct_name(product.product_name());
                productToUpdate.setStock(product.stock());
                productToUpdate.setPrice(product.price());
                productToUpdate.setImage(product.image());
                productToUpdate.setDate(product.expected_date_of_arrival());
                productToUpdate.setCategory(categoryRespository.findCategoryByCategory(product.categoryname()));
                productRespository.save(productToUpdate);
                return ResponseEntity.ok("Product with ID " + id + " updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the product.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateProductInfo(
            @PathVariable Integer id,
            @RequestBody HashMap<String, Object> updates
    ) {
        Product productToUpdate = productRespository.findProductById(id);
        if (productToUpdate != null) {
            if (updates.containsKey("product_name")) {
                productToUpdate.setProduct_name((String) updates.get("product_name"));
            }
            if (updates.containsKey("stock")) {
                productToUpdate.setStock((Integer) updates.get("stock"));
            }
            if (updates.containsKey("price")) {
                productToUpdate.setPrice((Float) updates.get("price"));
            }
            if (updates.containsKey("image")) {
                productToUpdate.setImage((String) updates.get("image"));
            }
            if (updates.containsKey("date")) {
                productToUpdate.setDate((String) updates.get("date"));
            }
            if (updates.containsKey("rating")) {
                productToUpdate.setRating(Float.parseFloat(String.valueOf(updates.get("rating"))));
            }
            productRespository.save(productToUpdate);
            return ResponseEntity.ok("Product with ID " + id + " updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

