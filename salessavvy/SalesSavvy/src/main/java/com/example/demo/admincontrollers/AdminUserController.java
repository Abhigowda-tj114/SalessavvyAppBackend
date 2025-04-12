// Admin Controller for Modify User functionality
package com.example.demo.admincontrollers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminservices.AdminUserService;
import com.example.demo.entity.User;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RequestMapping("/admin/user")
public class AdminUserController {
   
	AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		
		this.adminUserService = adminUserService;
	}
	
	@PostMapping("/getbyid")
	public ResponseEntity<?> getUserById(@RequestBody Map<String, Integer> userRequest) {
		try {
		int userId = userRequest.get("userId");
		User user = adminUserService.getUserById(userId);
		return ResponseEntity.status(HttpStatus.OK).body(user);
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong");
		}
	}
	
	@PutMapping("/modify")
	public ResponseEntity<?> modifyUser(@RequestBody Map<String, Object> userRequest) {
		
		try {
			Integer userId=(Integer)userRequest.get("userId");
			String username = (String)userRequest.get("username");
			String email = (String)userRequest.get("email");
			String role = (String)userRequest.get("role");
			
			User updateUser=adminUserService.modifyUser(userId, username, email, role);
			Map<String, Object> response = new HashMap<>();
			response.put("userId", updateUser.getUserId());
			response.put("username", updateUser.getUsername());
			response.put("email", updateUser.getEmail());
			response.put("role", updateUser.getRole().name());
			response.put("createdAt", updateUser.getCreatedAt());
			response.put("updatedAt", updateUser.getUpdatedAt());
			
			return ResponseEntity.status(HttpStatus.OK).body(response);
			
		}catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong");
		}
	}
	
	
}