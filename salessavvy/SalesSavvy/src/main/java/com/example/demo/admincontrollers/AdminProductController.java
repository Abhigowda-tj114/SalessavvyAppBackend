// Admin Controller for Add Product functionality
package com.example.demo.admincontrollers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminservices.AdminProductService;
import com.example.demo.entity.Product;

@RestController
@RequestMapping("/admin/products")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class AdminProductController {

  AdminProductService adminProductService;

public AdminProductController(AdminProductService adminProductService) {
	super();
	this.adminProductService = adminProductService;
}
  
  @PostMapping("/add")
  public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> productRequest) {
	  try {
	  String name = (String) productRequest.get("name");
	  String description = (String) productRequest.get("description");
	  Double price = Double.valueOf(String.valueOf(productRequest.get("price")));
	  Integer stock = (Integer) productRequest.get("stock");
	  Integer categoryId = (Integer) productRequest.get("categoryId");
	  String imageUrl  = (String) productRequest.get("imageUrl");
	  
	Product addedProduct= adminProductService.addProductWithImage(name, description, price, stock, categoryId, imageUrl);
	
	Map<String, Object> response = new HashMap<>();
	response.put("product", addedProduct);
	response.put("imageUrl",imageUrl);
	  return ResponseEntity.status(HttpStatus.CREATED).body(response);
	  }catch(IllegalArgumentException e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	  }
	  catch (Exception e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went Wrong");
	}
  }
  
  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteProduct(@RequestBody Map<String, Object> requestBody) {
	  try {
		 Integer productId = (Integer) requestBody.get("productId");
		
		 adminProductService.deleteProduct(productId);
		 return ResponseEntity.status(HttpStatus.OK).body("Product deleted Successfully");
	}  
	  catch (Exception e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went Wrong");
	}
  }
}