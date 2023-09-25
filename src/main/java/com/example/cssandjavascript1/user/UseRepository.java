package com.example.cssandjavascript1.user;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface UseRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    @Query("SELECT u FROM User u")
    List<User> findAllUsers();
}