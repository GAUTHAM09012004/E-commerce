package com.example.cssandjavascript1.Order;

import com.example.cssandjavascript1.Order.Order;
import com.example.cssandjavascript1.Product.Product;
import com.example.cssandjavascript1.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Integer> {
    public Order deleteOrderByUserAndProduct(User user, Product product);
}
