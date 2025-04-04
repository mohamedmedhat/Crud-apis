package com.example.apis.grpc_api;

import com.example.apis.UserRequest;
import com.example.apis.UserResponse;
import com.example.apis.common.models.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class UserServiceGrpcMapper {

    public UserResponse toUserResponse(User savedUser){
        return  UserResponse.newBuilder()
                .setId(savedUser.getId().toString())
                .setName(savedUser.getName())
                .setAge(savedUser.getAge())
                .setRate(savedUser.getRate().toString())
                .build();
    }

    public User toUserEntity(UserRequest request){
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setRate(new BigDecimal(request.getRate()));
        return user;
    }
}
