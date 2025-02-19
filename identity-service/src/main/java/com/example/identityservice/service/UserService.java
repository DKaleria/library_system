package com.example.identityservice.service;

import com.example.identityservice.database.entity.RegistrationRequest;
import com.example.identityservice.database.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserService extends UserDetailsService {
    User register(RegistrationRequest request);
    List<User> getAllUsers();
    User getCurrentUser() throws ExecutionException, InterruptedException;
}