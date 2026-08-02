package com.shopkart.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shopkart.dto.ProductRequest;
import com.shopkart.dto.ProductResponse;
import com.shopkart.entity.Category;
import com.shopkart.entity.Product;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.exception.ProductNotFoundException;
import com.shopkart.repository.CategoryRepository;
import com.shopkart.repository.ProductRepository;
import com.shopkart.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	public final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	// constructor
	public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;

	}

	@Override
	public ProductResponse createProduct(ProductRequest request, MultipartFile image) {

	    Category category = categoryRepository.findById(request.getCategoryId())
	            .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

	    String fileName = null;

	    if (image != null && !image.isEmpty()) {

	        try {

	            // uploads folder project root-la create aagum
	            Path uploadPath = Paths.get("uploads");

	            if (!Files.exists(uploadPath)) {
	                Files.createDirectories(uploadPath);
	            }

	            fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

	            Files.copy(
	                    image.getInputStream(),
	                    uploadPath.resolve(fileName),
	                    StandardCopyOption.REPLACE_EXISTING
	            );

	            System.out.println("Image Saved Successfully : " + uploadPath.resolve(fileName));

	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new RuntimeException("Image Upload Failed");
	        }

	    }

	    Product product = new Product();

	    product.setName(request.getName());
	    product.setDescription(request.getDescription());
	    product.setPrice(request.getPrice());
	    product.setStock(request.getStock());
	    product.setCategory(category);
	    product.setImageUrl(fileName);

	    Product savedProduct = productRepository.save(product);

	    ProductResponse response = new ProductResponse();

	    response.setId(savedProduct.getId());
	    response.setName(savedProduct.getName());
	    response.setDescription(savedProduct.getDescription());
	    response.setPrice(savedProduct.getPrice());
	    response.setStock(savedProduct.getStock());
	    response.setImageUrl(savedProduct.getImageUrl());
	    response.setActive(savedProduct.getActive());
	    response.setCategoryId(savedProduct.getCategory().getId());
	    response.setCategoryName(savedProduct.getCategory().getName());

	    return response;
	}
	
	
	@Override
	public List<ProductResponse> getAllProducts() {

		List<Product> products = productRepository.findAll();

		return products.stream().map(product -> {

			ProductResponse response = new ProductResponse();

			response.setId(product.getId());
			response.setName(product.getName());
			response.setDescription(product.getDescription());
			response.setPrice(product.getPrice());
			response.setStock(product.getStock());
			response.setImageUrl(product.getImageUrl());
			response.setActive(product.getActive());
			response.setCategoryName(product.getCategory().getName());
			response.setCategoryId(product.getCategory().getId());
			

			return response;

		}).toList();
	}

	@Override
	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Product not found"));

		ProductResponse response = new ProductResponse();

		response.setId(product.getId());
		response.setName(product.getName());
		response.setDescription(product.getDescription());
		response.setPrice(product.getPrice());
		response.setStock(product.getStock());
		response.setImageUrl(product.getImageUrl());
		response.setActive(product.getActive());
		response.setCategoryName(product.getCategory().getName());
		response.setCategoryId(product.getCategory().getId());

		return response;
	}

	@Override
	public ProductResponse updateProduct(Long id,
	                                     ProductRequest request,
	                                     MultipartFile image) {

	    Product product = productRepository.findById(id)
	            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

	    Category category = categoryRepository.findById(request.getCategoryId())
	            .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

	    product.setName(request.getName());
	    product.setDescription(request.getDescription());
	    product.setPrice(request.getPrice());
	    product.setStock(request.getStock());

	    if (image != null && !image.isEmpty()) {

	        try {

	            Path uploadPath = Paths.get("src/main/resources/static/images");

	            if (!Files.exists(uploadPath)) {
	                Files.createDirectories(uploadPath);
	            }

	            String fileName = image.getOriginalFilename();

	            Files.copy(
	                    image.getInputStream(),
	                    uploadPath.resolve(fileName),
	                    StandardCopyOption.REPLACE_EXISTING
	            );

	            product.setImageUrl(fileName);

	        } catch (IOException e) {
	            throw new RuntimeException("Image Upload Failed");
	        }

	    }

	    product.setCategory(category);

	    Product saved = productRepository.save(product);

	    ProductResponse response = new ProductResponse();

	    response.setId(saved.getId());
	    response.setName(saved.getName());
	    response.setDescription(saved.getDescription());
	    response.setPrice(saved.getPrice());
	    response.setStock(saved.getStock());
	    response.setImageUrl(saved.getImageUrl());
	    response.setActive(saved.getActive());
	    response.setCategoryId(saved.getCategory().getId());
	    response.setCategoryName(saved.getCategory().getName());

	    return response;
	}

	@Override
	public void deleteProduct(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow( ()-> new CategoryNotFoundException("Product not found"));
		
		productRepository.delete(product);

	}

}
