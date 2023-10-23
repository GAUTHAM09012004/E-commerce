package com.example.cssandjavascript1.Product;

import com.example.cssandjavascript1.Order.Order;
import com.example.cssandjavascript1.Order.OrderRepo;
import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.shipping.Shipping;
import com.example.cssandjavascript1.shipping.Shippingrepo;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart Controller")
public class CartController {
    private final UseRepository userRepository;
    private final ProductRespository productRespository;
    private final JwtService jwtService;
    private final OrderRepo orderRepo;
    private final Shippingrepo shippingRepo;

    @Operation(
            description = "Get Endpoint for User Cart",
            summary = "Returns a List of Products in the User's Cart"
    )
    @GetMapping
    public List<Product> Displayallusercart(@RequestHeader("Authorization") String jwtToken) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = userRepository.findByEmail(username);
            List<Product> products = user.getCart();
            return products;
        } catch (Exception e) {
            return null;
        }
    }

    @Operation(
            description = "Get Endpoint for Checkout of products in User Cart",
            summary = "Checkouts all products in the User's Cart and proceeds to payment"
    )
    @GetMapping("/checkout")
    public ResponseEntity<String> checkoutProducts(@RequestHeader("Authorization") String jwtToken) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = userRepository.findByEmail(username);
            List<Product> cart = new ArrayList<>(user.getCart()); // Create a copy of the cart to avoid concurrent modification

            for (Product product : cart) {
                ProductCheckout(user, product);
            }

            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Checkout failed: " + e.getMessage());
        }

        return ResponseEntity.status(200).body("Checkout successful");
    }

    @Operation(
            description = "Get Endpoint for Checkout of a product in User Cart",
            summary = "Checkouts a product in the User's Cart and proceeds to payment"
    )
    @GetMapping("/checkout/{id}")
    public ResponseEntity<String> checkoutProduct(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable("id") int productId
    ) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = userRepository.findByEmail(username);
            Product productToCheckout = null;

            for (Product product : user.getCart()) {
                if (product.getId().equals(productId)) {
                    productToCheckout = product;
                    break;
                }
            }

            if (productToCheckout != null) {
                ProductCheckout(user, productToCheckout);
                userRepository.save(user);
                return ResponseEntity.status(200).body("Checkout successful");
            } else {
                return ResponseEntity.status(404).body("Product not found in cart");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Checkout failed: " + e.getMessage());
        }
    }

    private void ProductCheckout(User user, Product productToCheckout) {
        user.getProducts().add(productToCheckout);
        user.getCart().remove(productToCheckout);
        productToCheckout.setStock(productToCheckout.getStock() - 1);
        Order order = Order.builder()
                .user(user)
                .product(productToCheckout)
                .quantity(1)
                .build();
        Shipping shipping = Shipping.builder()
                .user(user)
                .product(productToCheckout)
                .shippingdate(productToCheckout.getDate())
                .build();
        shippingRepo.save(shipping);
        orderRepo.save(order);
        productRespository.save(productToCheckout);
    }


    @Operation(
            description = "Post Endpoint for User Cart",
            summary = "Adds a Product to the User's Cart"
    )
    @PostMapping("/{id}")
    public ResponseEntity<String> addProductToUser(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String jwtToken
    ) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = userRepository.findByEmail(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            Product product = productRespository.findProductById(id);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            user.getCart().add(product);
            userRepository.save(user);
            return ResponseEntity.ok("Product added to user cart.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the product to the user.");
        }
    }

    @Operation(
            description = "Delete Endpoint for User Cart",
            summary = "Deletes a Product from the User's Cart"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeProductFromUser(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String jwtToken
    ) {
        try {
            String username = jwtService.extractUserEmailFromJwt(jwtToken);
            User user = userRepository.findByEmail(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            Product product = productRespository.findProductById(id);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            user.getCart().remove(product);
            userRepository.save(user);
            return ResponseEntity.ok("Product removed from user cart.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the product from the user.");
        }
    }
}
