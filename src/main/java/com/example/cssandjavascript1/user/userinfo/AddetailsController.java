package com.example.cssandjavascript1.user.userinfo;

import com.example.cssandjavascript1.config.JwtService;
import com.example.cssandjavascript1.user.UseRepository;
import com.example.cssandjavascript1.user.User;
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
public class AddetailsController {

    private final UserInfoRespository userInfoRepository;
    private final UseRepository repository;
    private final JwtService jwtService;

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
}
