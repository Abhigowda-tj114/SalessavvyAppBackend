// Admin Service for Modify User functionality
package com.example.demo.adminservices;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.JWTTokenRepository;
import com.example.demo.repository.UserRepository;

@Service
public class AdminUserService {
	
	UserRepository userRepository;
	JWTTokenRepository jwtTokenRepository;
	
	
	public AdminUserService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository) {
		
		this.userRepository = userRepository;
		this.jwtTokenRepository = jwtTokenRepository;
	}


	public User getUserById(Integer userId) {
		return userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User with userId Not Found"));
	}
	
	
	public User modifyUser(Integer userId, String name, String email,String role) {
		Optional<User> userOptional=userRepository.findById(userId);
		
		if(userOptional.isEmpty()) {
			throw new IllegalArgumentException("User with Id Not Found");
		}
		User exiatingUser = userOptional.get();
		
		if(name != null && !name.isEmpty()) {
			exiatingUser.setUsername(name);
		}
		
		if(email != null && !email.isEmpty()) {
			exiatingUser.setEmail(email);
		}
		if(role != null && !role.isEmpty()) {
			exiatingUser.setRole(Role.valueOf(role));
		}
		
		jwtTokenRepository.deleteByUserId(exiatingUser.getUserId());	
		return userRepository.save(exiatingUser);
		
	}
}