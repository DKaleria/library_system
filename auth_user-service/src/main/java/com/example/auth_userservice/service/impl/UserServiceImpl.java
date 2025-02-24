package com.example.auth_userservice.service.impl;

import com.example.auth_userservice.AuthUser;
import com.example.auth_userservice.config.IdentityServiceClient;
import com.example.auth_userservice.database.entity.User;
import com.example.auth_userservice.database.repository.UserRepository;
import com.example.auth_userservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.auth_userservice.AuthUserServiceGrpc;

@Service
@GrpcService
@Transactional
public class UserServiceImpl extends AuthUserServiceGrpc.AuthUserServiceImplBase implements UserService {
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

    @Override
    public void checkUserExists(AuthUser.CheckUserRequest request, StreamObserver<AuthUser.CheckUserResponse> responseObserver) {
        Long userId = request.getUserId();
        boolean exists = userRepository.findById(userId).isPresent();

        AuthUser.CheckUserResponse response = AuthUser.CheckUserResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}