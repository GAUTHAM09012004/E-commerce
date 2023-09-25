package com.example.cssandjavascript1.auth;
public record RegisterExtraInfoRequest(
        String city,
        String state,
        String zip,
        String date,
        String phone_number,
        String address1,
        String address2,
        String country
) {}

