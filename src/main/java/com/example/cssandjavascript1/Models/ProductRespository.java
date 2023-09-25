package com.example.cssandjavascript1.Models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProductRespository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p")
    List<Product> findAllProducts();

    Product findProductById(Integer id);
}
