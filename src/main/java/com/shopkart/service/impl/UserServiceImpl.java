package com.shopkart.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shopkart.dto.UserRequest;
import com.shopkart.dto.UserResponse;
import com.shopkart.dto.UserUpdateRequest;
import com.shopkart.entity.User;
import com.shopkart.exception.UserNotFoundException;
import com.shopkart.repository.UserRepository;
import com.shopkart.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	
	private final UserRepository userRepository;
	private final Cloudinary cloudinary;
		
	public UserServiceImpl(UserRepository userRepository,
			Cloudinary cloudinary) {
		
		this.userRepository=userRepository;
		this.cloudinary = cloudinary;

	}
	
	private UserResponse convertToResponse(User user) {

	    UserResponse response = new UserResponse();

	    response.setId(user.getId());
	    response.setFirstName(user.getFirstName());
	    response.setLastName(user.getLastName());
	    response.setEmail(user.getEmail());
	    response.setPhone(user.getPhone());
	    response.setRole(user.getRole());
	    response.setActive(user.getActive());
	    response.setCreatedAt(user.getCreatedAt());
	    response.setUpdatedAt(user.getUpdatedAt());
	    response.setProfileImage(user.getProfileImage());

	    // profile image irundha
	    response.setProfileImage(user.getProfileImage());

	    return response;
	}

	@Override
	public UserResponse createUser(UserRequest request) {
		// check if email already Exist
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already exists");
		}
		
		// request DTO -> Entity
		User user=new User();
		
		user.setFirstName(request.getFirstName());
	    user.setLastName(request.getLastName());
	    user.setEmail(request.getEmail());
	    user.setPassword(request.getPassword());
	    user.setPhone(request.getPhone());
	    user.setRole(request.getRole());
	    
	    //Saved to Database
	    User savedUser=userRepository.save(user);
	    
	    // Entity -> Response DTO
	    UserResponse response=new UserResponse();
	    
	    response.setId(savedUser.getId());
	    response.setFirstName(savedUser.getFirstName());
	    response.setLastName(savedUser.getLastName());
	    response.setEmail(savedUser.getEmail());
	    response.setPhone(savedUser.getPhone());
	    response.setRole(savedUser.getRole());
	    response.setActive(savedUser.getActive());
	    response.setCreatedAt(savedUser.getCreatedAt());
	    response.setUpdatedAt(savedUser.getUpdatedAt());
	    
	    response.setProfileImage(savedUser.getProfileImage());
	    
	    return response;
	}

	@Override
	public List<UserResponse> getAllUsers() {
		
		List<User> users=userRepository.findAll();
		
		return users.stream().map(user ->{
			
			UserResponse response = new UserResponse();
			
			response.setId(user.getId());
			response.setFirstName(user.getFirstName());
			response.setLastName(user.getLastName());
			response.setEmail(user.getEmail());
			response.setPhone(user.getPhone());
			response.setRole(user.getRole());
			response.setActive(user.getActive());
			response.setCreatedAt(user.getCreatedAt());
			response.setUpdatedAt(user.getUpdatedAt());
			
			response.setProfileImage(user.getProfileImage());
			
			return response;
			
			
		}).toList();
	}

	@Override
	public UserResponse getUserById(Long id) {
		
		User user = userRepository.findById(id)
				.orElseThrow( ()-> new UserNotFoundException("User not found"));
		
		UserResponse response=new UserResponse();
		
		response.setId(user.getId());
	    response.setFirstName(user.getFirstName());
	    response.setLastName(user.getLastName());
	    response.setEmail(user.getEmail());
	    response.setPhone(user.getPhone());
	    response.setRole(user.getRole());
	    response.setActive(user.getActive());
	    response.setCreatedAt(user.getCreatedAt());
	    response.setUpdatedAt(user.getUpdatedAt());
	    
	    response.setProfileImage(user.getProfileImage());

	    return response;
		
	}

	@Override
	public UserResponse updateUser(
	        Long id,
	        UserUpdateRequest request) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() ->
	                    new RuntimeException(
	                            "User not found: " + id
	                    )
	            );

	    user.setFirstName(request.getFirstName());
	    user.setLastName(request.getLastName());
	    user.setEmail(request.getEmail());
	    user.setPhone(request.getPhone());

	    User savedUser = userRepository.save(user);

	    return convertToResponse(savedUser);
	}

	@Override
	public void deleteUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("user not found"));
		
		userRepository.delete(user);
		
	}
	@Override
	public UserResponse uploadProfileImage(Long id, MultipartFile image) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    try {

	        Map uploadResult = cloudinary.uploader().upload(
	                image.getBytes(),
	                ObjectUtils.asMap(
	                        "folder", "profiles"
	                )
	        );

	        String imageUrl = uploadResult.get("secure_url").toString();

	        user.setProfileImage(imageUrl);

	        userRepository.save(user);

	        return convertToResponse(user);

	    } catch (IOException e) {

	        throw new RuntimeException("Profile image upload failed");

	    }

	}
}
