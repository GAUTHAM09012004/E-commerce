package com.example.cssandjavascript1.auth;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

//ResponseEntity<String> is a json format return type
//ResponseEntity is a class provided by Spring that represents an HTTP response
// with a specific status code
//ResponseEntity.ok(...) is used to create an HTTP response entity with a status code
// of 200 OK and a body containing the provided content.
// It's a way to wrap the data you want to send back to the client in an HTTP response.

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(
            description = "Post Endpoint for Registration",
            summary = "returns a otp for verification"
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {
        return service.register(request);
    }

    @Operation(
            description = "Post Endpoint for Verification",
            summary = "Authenticates the otp and returns a jwt token"
    )
    @PostMapping("/verify")
    public ResponseEntity<AuthenticationResponse> verify(
            @RequestBody VerificationRequest verificationRequest
    ) {
        return ResponseEntity.ok(service.verifyCode(verificationRequest));
    }


    @Operation(
            description = "Post Endpoint for Authentication",
            summary = "Authenticates the user and returns a jwt token"
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(
            description = "Post Endpoint for Refresh Token",
            summary = "Refreshes the jwt token and returns a new jwt token"
    )
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

}