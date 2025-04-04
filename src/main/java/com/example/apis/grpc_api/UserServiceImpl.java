package com.example.apis.grpc_api;

import com.example.apis.common.exceptions.UserNotFoundException;
import com.example.apis.common.models.User;
import com.example.apis.common.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateProduct(User user) {
        if (userRepository.existsById(user.getId())) {
            return userRepository.save(user);
        }
        throw new UserNotFoundException("User with id: " + user.getId() + " not found");
    }

    public boolean deleteProduct(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
