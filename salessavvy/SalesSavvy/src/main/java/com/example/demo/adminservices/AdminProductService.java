package com.example.demo.adminservices;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class AdminProductService {

   ProductRepository productRepository;
   ProductImageRepository productImageRepository;
   CategoryRepository categoryRepository;
public AdminProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
		CategoryRepository categoryRepository) {

	this.productRepository = productRepository;
	this.productImageRepository = productImageRepository;
	this.categoryRepository = categoryRepository;
}
   
   public Product  addProductWithImage(String name, String description, Double price, Integer stock,
		   Integer categoryId, String imageUrl) {
	  Optional<Category> category= categoryRepository.findById(categoryId);
	   
	  if(category.isEmpty()) {
		  throw new IllegalArgumentException("Invalid category Id");
	  }
	  
	  Product product = new Product(name, description, BigDecimal.valueOf(price), stock, category.get(), LocalDateTime.now(), LocalDateTime.now());
	Product savedProduct =   productRepository.save(product);
	  
	if(imageUrl != null && !imageUrl.isEmpty()) {
		ProductImage productImage=new ProductImage(savedProduct, imageUrl);
		productImageRepository.save(productImage);
	}
	else {
		throw new IllegalArgumentException("Product image cannot Be Empty");
	}
	   return savedProduct;
   }
   
  

public void deleteProduct(Integer productId) {
	// TODO Auto-generated method stub
	  productImageRepository.deleteByProductId(productId);
	   productRepository.deleteById(productId);
}
}