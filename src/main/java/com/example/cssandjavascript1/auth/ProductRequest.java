package com.example.cssandjavascript1.auth;
public record ProductRequest(
        String product_name,
        int stock,
        float price,
        float rating,
        String image,
        String expected_date_of_arrival,
        String categoryname
        ) {}

