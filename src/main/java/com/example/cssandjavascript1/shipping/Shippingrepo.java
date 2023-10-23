package com.example.cssandjavascript1.shipping;

import com.example.cssandjavascript1.Product.Product;
import com.example.cssandjavascript1.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Shippingrepo extends JpaRepository<Shipping, Integer>
{
    public Shipping deleteShippingByUserAndProduct(User user, Product product);

}
