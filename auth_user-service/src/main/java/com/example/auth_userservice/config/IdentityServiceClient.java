package com.example.auth_userservice.config;

import com.example.auth_userservice.database.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "identity-service", url = "http://identity-service:8088")
public interface IdentityServiceClient {
    @GetMapping("/identity/users")
    List<User> getUsers(@RequestHeader("Authorization") String token);

    @GetMapping("/identity/currentUser")
    User getCurrentUser(@RequestHeader("Authorization") String token);
}