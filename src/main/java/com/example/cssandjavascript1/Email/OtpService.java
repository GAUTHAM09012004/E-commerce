package com.example.cssandjavascript1.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    public HashMap<String, String> map = new HashMap<>();

    public String generateAndStoreOtp(String userId) {
        String otp = generateRandomOtp();
        map.put(userId, otp);
        return otp;
    }

    public String getStoredOtp(String userId) {
        return map.get(userId);
    }

    private String generateRandomOtp() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }
}

