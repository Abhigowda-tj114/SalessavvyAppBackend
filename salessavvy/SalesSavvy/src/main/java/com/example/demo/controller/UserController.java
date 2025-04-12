package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/getbyname")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    // Update user profile
    @PutMapping("/modifyuser")
    public ResponseEntity<?> modifyUser(@RequestBody User updatedUser) {
        try {
            User modifiedUser = userService.modifyUser(updatedUser);
            return ResponseEntity.ok(modifiedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Upload avatar
    @PostMapping("/uploadavatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) {
        if (avatar.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        // Simulate saving the file and returning URL (In real app, store it on cloud)
        String avatarUrl = "/uploads/" + avatar.getOriginalFilename();
        return ResponseEntity.ok().body("{\"avatarUrl\": \"" + avatarUrl + "\"}");
    }

}