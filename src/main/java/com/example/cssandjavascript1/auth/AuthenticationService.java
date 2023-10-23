package com.example.cssandjavascript1.auth;


import com.example.cssandjavascript1.Email.EmailService;
import com.example.cssandjavascript1.Email.OtpService;
import com.example.cssandjavascript1.user.User;
import com.example.cssandjavascript1.token.Token;
import com.example.cssandjavascript1.token.TokenRepository;
import com.example.cssandjavascript1.token.TokenType;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.config.JwtService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.cssandjavascript1.Email.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

//var keyword is used to declare local variables.
// that allows you to declare variables without explicitly specifying their data types.
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UseRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final EmailService emailService;
    static User tempuser;


    //in this method, we are getting all the details of the user, and we are saving
    //them to the database, we are also sending them a jwt token and refresh token
    //for authentication and authorization protected resources
    //we first have to get user details and get otp to the mail given below


    public ResponseEntity<String> register(RegisterRequest request) {

        tempuser = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        String otp = otpService.generateAndStoreOtp(tempuser.getEmail());
        String message = "Your OTP is " + otp;
        emailService.send(tempuser.getEmail(), message);
        return ResponseEntity.ok("OTP has been sent to your email. Enter the code at /verify endpoint.");
    }

    public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) {
        String userId = String.valueOf(verificationRequest.getEmail());
        String storedOtp = otpService.getStoredOtp(userId);
        if (storedOtp == null || !storedOtp.equals(verificationRequest.getCode())) {
            throw new BadCredentialsException("Code is not correct");
        }
        otpService.map.remove(userId);
        repository.save(tempuser);
        var savedUser = repository.save(tempuser);
        var jwtToken = jwtService.generateToken(tempuser);
        var refreshToken = jwtService.generateRefreshToken(tempuser);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    //here the Authenticate manager authenticates the details, if authenticated
    //new jwt token and refresh token are created bases on user details,
    //and all old tokens are revoked

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //if false, it throws an authentication exception
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = repository.findByEmail(request.email());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    //this to save the jwt token to user whenever a new jwt token is created,
    //when we register and authenticate, we create both refresh token and access token,
    //during authentication we revoke all old tokens

    //one to many mapping
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    //this is to delete all the old tokens related to the user after new tokens are created

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    //So There are two types of tokens here,one is access token, and one is refresh token
    //when access token is over;u can use refresh tokens to generated new access tokens

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        //HttpHeaders.AUTHORIZATION usually contains the jwt token
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                //this is http response
            }
        }
    }

}
