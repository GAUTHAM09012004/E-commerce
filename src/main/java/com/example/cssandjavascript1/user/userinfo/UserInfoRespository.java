package com.example.cssandjavascript1.user.userinfo;

import com.example.cssandjavascript1.user.userinfo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRespository extends JpaRepository<UserInfo, Integer>
{
    Optional<UserInfo> findById(Integer id);
}
