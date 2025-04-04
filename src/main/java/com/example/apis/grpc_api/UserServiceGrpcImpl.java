package com.example.apis.grpc_api;

import com.example.apis.*;
import com.example.apis.common.exceptions.UserNotFoundException;
import com.example.apis.common.repositories.UserRepository;
import com.example.apis.common.models.User;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository userRepository;
    private final UserServiceGrpcMapper userServiceGrpcMapper;

    @PostConstruct
    public void init() {
        log.info("🚀 gRPC UserService is running...");
    }

    @Override
    public void createUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("Received gRPC request: {}", request.getName()); // Add logging
        User user = this.userServiceGrpcMapper.toUserEntity(request);
        User savedUser = userRepository.save(user);
        UserResponse response = this.userServiceGrpcMapper.toUserResponse(savedUser);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UUID userId = UUID.fromString(request.getId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setRate(new BigDecimal(request.getRate()));

        User updatedUser = userRepository.save(user);

        UserResponse response = this.userServiceGrpcMapper.toUserResponse(updatedUser);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(UserIdRequest request, StreamObserver<DeleteResponse> responseObserver) {
        UUID userId = UUID.fromString(request.getId());
        boolean exists = userRepository.existsById(userId);

        if (exists) {
            userRepository.deleteById(userId);
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User deleted successfully")
                    .build());
        } else {
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void listUsers(Empty request, StreamObserver<UserList> responseObserver) {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(this.userServiceGrpcMapper::toUserResponse)
                .toList();

        UserList response = UserList.newBuilder().addAllUsers(users).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
