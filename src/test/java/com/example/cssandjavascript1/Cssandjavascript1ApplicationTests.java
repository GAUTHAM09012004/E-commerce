package com.example.cssandjavascript1;

import com.example.cssandjavascript1.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.cssandjavascript1.user.UseRepository;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@SpringBootTest
class Cssandjavascript1ApplicationTests {
	@Autowired
	private UseRepository userRepository;

	@Test
	void contextLoads() {
	}


}
