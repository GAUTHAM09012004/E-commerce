package com.example.cssandjavascript1.Product;

import com.example.cssandjavascript1.Product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRespository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p")
    List<Product> findAllProducts();

    Product findProductById(Integer id);
}
