package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public User registerUser(User user) {
        // Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the user
        return userRepository.save(user);
    }
    
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    
    
    public User modifyUser(User updatedUser) {
        Optional<User> userOptional = userRepository.findById(updatedUser.getUserId());

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
           
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}