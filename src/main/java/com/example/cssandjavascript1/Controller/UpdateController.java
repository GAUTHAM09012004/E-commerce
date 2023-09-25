package com.example.cssandjavascript1.Controller;

import com.example.cssandjavascript1.Email.EmailService;
import com.example.cssandjavascript1.Email.OtpService;
import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class UpdateController
{
    private final JwtService jwtService;
    private final UseRepository repository;
    private final OtpService otpService;
    private final EmailService emailService;
    static String password;
    @PatchMapping("/Updatedetails")
    public ResponseEntity<String> updateUser(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String,Object> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user1 = repository.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.ok("User not found");
        }
        else
        {
            if (updates.containsKey("firstname")) {
                user1.setFirstname((String)updates.get("firstname"));
            }
            if (updates.containsKey("lastname")) {
                user1.setFirstname((String)updates.get("lastname"));
            }

        }
        repository.save(user1);
        return ResponseEntity.ok("UserInfo updated successfully");
    }
    @PostMapping("/UpdatePasssword")
    public ResponseEntity<String> updatePassword(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String,Object> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user1 = repository.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.ok("User not found");
        }
        else
        {
            if (updates.containsKey("passsword"))
            {
                String otp = otpService.generateAndStoreOtp(user1.getEmail());
                password= (String) updates.get("password");
                String message = "Your OTP for password change is " + otp;
                emailService.send(user1.getEmail(), message);
                return ResponseEntity.ok("OTP has been sent to your email. Enter the code at /verifypassword endpoint.");
            }
            else
            {
                ResponseEntity.badRequest();
            }

        }
        repository.save(user1);
        return ResponseEntity.ok("UserInfo updated successfully");
    }
    @PostMapping("/UpdatePassswordotp")
    public ResponseEntity<String> VerifyCodePassword(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String,String> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user = repository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        String storedOtp = otpService.getStoredOtp(String.valueOf(user));
        String otp = updates.get("otp");
        if(otp.equals(storedOtp))
        {
            user.setPassword(password);
            repository.save(user);
            return ResponseEntity.ok("Password updated successfully");
        }
        return ResponseEntity.ok("Code given is not correct");
    }

}
