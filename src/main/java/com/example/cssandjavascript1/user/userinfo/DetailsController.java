package com.example.cssandjavascript1.user.userinfo;

import com.example.cssandjavascript1.Email.EmailService;
import com.example.cssandjavascript1.Email.OtpService;
import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Details Controller")
public class DetailsController {

    private final UserInfoRespository userInfoRepository;
    private final UseRepository repository;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;
    String password;

    @Operation(
            description = "Post Endpoint for User Details",
            summary = "Adds the User Details to the Database"
    )
    @PostMapping("/AddDetails")
    public ResponseEntity<String> addUserInfo(@RequestHeader("Authorization") String jwtToken, @RequestBody RegisterExtraInfoRequest userinfo) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user1 = repository.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.ok("User not found");
        }
        UserInfo existingUserInfo = new UserInfo();
        existingUserInfo.setAddress_line1(userinfo.address1());
        existingUserInfo.setAddress_line2(userinfo.address2());
        existingUserInfo.setCity(userinfo.city());
        existingUserInfo.setDate(userinfo.date());
        existingUserInfo.setState(userinfo.state());
        existingUserInfo.setCountry(userinfo.country());
        existingUserInfo.setZip(userinfo.zip());
        existingUserInfo.setPhone_number(userinfo.phone_number());
        user1.setUserInfo(existingUserInfo);
        userInfoRepository.save(existingUserInfo);
        return ResponseEntity.ok("UserInfo updated successfully");
    }

    @Operation(
            description = "Patch Endpoint for User Details",
            summary = "Updates the User Details in the Database"
    )
    @PatchMapping("/UpdateUserdetails")
    public ResponseEntity<String> updateUserInfo(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String, Object> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user1 = repository.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.ok("User not found");
        }
        UserInfo existingUserInfo = user1.getUserInfo();
        if (existingUserInfo == null) {
            existingUserInfo = new UserInfo();
            user1.setUserInfo(existingUserInfo);
        }
        if (updates.containsKey("city")) {
            existingUserInfo.setCity((String) updates.get("city"));
        }
        if (updates.containsKey("state")) {
            existingUserInfo.setState((String) updates.get("state"));
        }
        if (updates.containsKey("zip")) {
            existingUserInfo.setZip((String) updates.get("zip"));
        }
        if (updates.containsKey("date")) {
            existingUserInfo.setDate((String) updates.get("date"));
        }
        if (updates.containsKey("phone_number")) {
            existingUserInfo.setPhone_number((String) updates.get("phone_number"));
        }
        if (updates.containsKey("address1")) {
            existingUserInfo.setAddress_line1((String) updates.get("address1"));
        }
        if (updates.containsKey("address2")) {
            existingUserInfo.setAddress_line2((String) updates.get("address2"));
        }
        if (updates.containsKey("country")) {
            existingUserInfo.setCountry((String) updates.get("country"));
        }
        userInfoRepository.save(existingUserInfo);
        return ResponseEntity.ok("UserInfo updated successfully");
    }

    @Operation(
            description = "Get Endpoint for User Details",
            summary = "Returns the User Details from the Database"
    )
    @GetMapping("/userdetails")
    public ResponseEntity<String> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            return ResponseEntity.ok("Current User: " + username);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
    }

    @Operation(
            description = "Patch Endpoint for User Details",
            summary = "Updates the User Details to the Database"
    )
    @PatchMapping("/Updatedetails")
    public ResponseEntity<String> updateUser(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String, Object> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user1 = repository.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.ok("User not found");
        } else {
            if (updates.containsKey("firstname")) {
                user1.setFirstname((String) updates.get("firstname"));
            }
            if (updates.containsKey("lastname")) {
                user1.setFirstname((String) updates.get("lastname"));
            }

        }
        repository.save(user1);
        return ResponseEntity.ok("UserInfo updated successfully");
    }

    @Operation(
            description = "Post Endpoint for User Password Update",
            summary = "Updates the User Password to the Database"
    )
    @PostMapping("/UpdatePasssword")
    public ResponseEntity<String> updatePassword(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String, Object> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user1 = repository.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.ok("User not found");
        } else {
            if (updates.containsKey("passsword")) {
                String otp = otpService.generateAndStoreOtp(user1.getEmail());
                password = (String) updates.get("password");
                String message = "Your OTP for password change is " + otp;
                emailService.send(user1.getEmail(), message);
                return ResponseEntity.ok("OTP has been sent to your email. Enter the code at /verifypassword endpoint.");
            } else {
                ResponseEntity.badRequest();
            }

        }
        repository.save(user1);
        return ResponseEntity.ok("UserInfo updated successfully");
    }

    @PostMapping("/UpdatePassswordotp")
    public ResponseEntity<String> VerifyCodePassword(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody HashMap<String, String> updates
    ) {
        String username = jwtService.extractUserEmailFromJwt(jwtToken);
        User user = repository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        String storedOtp = otpService.getStoredOtp(String.valueOf(user));
        String otp = updates.get("otp");
        if (otp.equals(storedOtp)) {
            user.setPassword(password);
            repository.save(user);
            return ResponseEntity.ok("Password updated successfully");
        }
        return ResponseEntity.ok("Code given is not correct");
    }
}
