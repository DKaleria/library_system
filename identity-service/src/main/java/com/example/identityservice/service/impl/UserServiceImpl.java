package com.example.identityservice.service.impl;

import com.example.identityservice.database.entity.CustomUserDetails;
import com.example.identityservice.database.entity.RegistrationRequest;
import com.example.identityservice.database.entity.User;
import com.example.identityservice.database.repository.UserRepository;
import com.example.identityservice.service.UserService;
import com.example.identityservice.usecaseses.mapper.AuthUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserMapper authUserMapper;
// private KafkaTemplate<String, AuthUserGotEvent> kafkaTemplate;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthUserMapper authUserMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authUserMapper = authUserMapper;
    }

    @Override
    public User register(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .birthDate(request.getBirthDate())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }

    @Override
    public User getCurrentUser() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user;
        }
        throw new RuntimeException("Authentication is not valid");
    }
}