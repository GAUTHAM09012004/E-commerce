package com.example.cssandjavascript1.Product;

import com.example.cssandjavascript1.Order.OrderRepo;
import com.example.cssandjavascript1.category.CategoryRepository;
import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.reviews.ReviewRepo;
import com.example.cssandjavascript1.reviews.ReviewRequest;
import com.example.cssandjavascript1.reviews.Reviews;
import com.example.cssandjavascript1.shipping.Shippingrepo;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller")
public class ProductController {

    private final JwtService jwtService;
    private final UseRepository useRepository;
    private final ProductRespository productRespository;
    private final CategoryRepository categoryRespository;
    private final OrderRepo orderRepo;
    private final Shippingrepo shippingRepo;
    private final ReviewRepo reviewRepo;

    @Operation(
            description = "Get endpoint for List of Products",
            summary = "Returns a List of all Products"

    )
    @GetMapping
    public List<Product> findallproducts() {
        return productRespository.findAllProducts();
    }

    @Operation(
            description = "Get endpoint for Product",
            summary = "Returns Product of the given id"

    )
    @GetMapping("/{id}")
    public Optional<Product> findproductsbyid(@PathVariable Integer id) {
        return productRespository.findById(id);
    }

    @Operation(
            description = "Delete endpoint for a Prouct",
            summary = "Deletes a Product of the given id,can only be done by admin"

    )
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> productsbyid(@PathVariable Integer id) {
        productRespository.deleteById(id);
        return ResponseEntity.ok("Product deleted");
    }

    @Operation(
            description = "Post endpoint for a Prouct",
            summary = "Creates a Product,All product details,must be given as a RequestBody" +
                    "can only be done by admin"
    )
    @PreAuthorize("hasAuthority('admin:create')")
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

    @Operation(
            description = "Put Endpoint for a Prouct",
            summary = "Updates a Product,All product details,must be given as a RequestBody" +
                    "can only be done by admin"
    )
    @PreAuthorize("hasAuthority('admin:update')")
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

    @Operation(
            description = "Patch Endpoint for a Prouct",
            summary = "Updates a Product,particular info about the product must be given as a RequestBody" +
                    "can only be done by admin"
    )
    @PreAuthorize("hasAuthority('admin:update')")
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

    @Operation(
            description = "Get Endpoint for a Product of a Particular User",
            summary = " Returns a List of Products of a particular user"
    )
    @GetMapping("/user")
    public ResponseEntity<List<Product>> findProductsOfUser(@RequestHeader("Authorization") String jwtToken) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = useRepository.findByEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Product> products = user.getProducts();

            if (products == null || products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            description = "Delete Endpoint for a Product of a Particular User",
            summary = " Deletes a Product of a particular user"
    )
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> DeleteProductsOfUser(@PathVariable Integer id, @RequestHeader("Authorization") String jwtToken) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = useRepository.findByEmail(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Product product = productRespository.findProductById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            user.getProducts().remove(product);
            orderRepo.deleteOrderByUserAndProduct(user, product);
            shippingRepo.deleteShippingByUserAndProduct(user, product);
            useRepository.save(user);
            return ResponseEntity.ok("Product deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(
            description = "Post Endpoint for a Product Review by a User",
            summary = " Adds a Review to a Product by a particular user and updates the rating of the product"
    )
    @PostMapping("/user/reviews/{id}")
    public ResponseEntity<String> givereviews(@PathVariable Integer id, @RequestHeader("Authorization") String jwtToken, @RequestBody ReviewRequest reviewDto) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = useRepository.findByEmail(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Product product = productRespository.findProductById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Float oldrating = product.getRating();
            oldrating += reviewDto.rating();
            oldrating /= 2;
            product.setRating(oldrating);
            Reviews reviews1 = Reviews.builder()
                    .rating(reviewDto.rating())
                    .reason(reviewDto.reason())
                    .user(user)
                    .product(product)
                    .build();
            product.getReviews().add(reviews1);
            reviewRepo.save(reviews1);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("Review added");
    }
}

