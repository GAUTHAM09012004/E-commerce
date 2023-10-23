package com.example.cssandjavascript1.Product;

import com.example.cssandjavascript1.category.Category;
import com.example.cssandjavascript1.Order.Order;
import com.example.cssandjavascript1.reviews.Reviews;
import com.example.cssandjavascript1.shipping.Shipping;
import com.example.cssandjavascript1.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_name")
    private String product_name;

    @Column(name = "stock")
    private int stock;

    @Column(name = "product_price")
    private Float price;

    @Column(name = "product_rating")
    private Float rating;

    @Column(name = "product_image")
    private String image;

    @Column(name = "expected_date_of_arrival")
    private String date;

    @JsonIgnore
    @JsonBackReference(value = "userproduct")
    @ManyToMany(mappedBy = "products")
    private List<User> users = new ArrayList<>();


    @JsonIgnore
    @JsonBackReference(value = "usercart")
    @ManyToMany(mappedBy = "cart")
    private List<User> userscart = new ArrayList<>();

    @JsonIgnore
    @JsonBackReference(value = "productshipping")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Shipping> shipping;

    @JsonBackReference(value = "productcategory")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonManagedReference("productreview")
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<Reviews> reviews;

    @JsonIgnore
    @JsonManagedReference("orderproduct")
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<Order> orders;

}
