package com.example.cssandjavascript1.auth;
import com.example.cssandjavascript1.user.Role;

public record RegisterRequest (
    String firstname,
    String lastname,
    String email,
    String password,
    Role role
){}