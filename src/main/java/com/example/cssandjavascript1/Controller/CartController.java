package com.example.cssandjavascript1.Controller;

import com.example.cssandjavascript1.Models.Product;
import com.example.cssandjavascript1.Models.ProductRespository;
import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final UseRepository userRepository;
    private final ProductRespository productRespository;
    private final JwtService jwtService;


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
    //getting stack overflow error

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
