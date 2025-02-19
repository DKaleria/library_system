package com.example.auth_userservice.service.impl;

import com.example.auth_userservice.config.IdentityServiceClient;
import com.example.auth_userservice.database.entity.User;
import com.example.auth_userservice.database.repository.UserRepository;
import com.example.auth_userservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final IdentityServiceClient identityServiceClient;

    public UserServiceImpl(UserRepository userRepository, IdentityServiceClient identityServiceClient) {
        this.userRepository = userRepository;
        this.identityServiceClient = identityServiceClient;
    }

    @CircuitBreaker(name = "identity-service")
    @Transactional
    @Override
    public List<User> searchUsers(String token) {
        List<User> usersFromService = identityServiceClient.getUsers(token);
        if (usersFromService != null) {
            for (User user : usersFromService) {
                if (!userRepository.findByUsername(user.getUsername()).isPresent()) {
                    userRepository.save(user);
                }
            }
        }
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @CircuitBreaker(name = "identity-service")
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(String token) {
        return identityServiceClient.getCurrentUser(token);
    }
}