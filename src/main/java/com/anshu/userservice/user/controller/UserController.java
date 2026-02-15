package com.anshu.userservice.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anshu.userservice.common.response.ApiResponse;
import com.anshu.userservice.user.dto.ChangePasswordRequest;
import com.anshu.userservice.user.dto.UpdateProfileRequest;
import com.anshu.userservice.user.dto.UserResponse;
import com.anshu.userservice.user.model.UserAccount;
import com.anshu.userservice.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication){
    	UUID userUuid = getUserUuid(authentication);
    	UserResponse account = userService.getProfile(userUuid);
    	return ResponseEntity.ok(new ApiResponse<>(true, "Profile Fetch", account));
    }
    
	@PutMapping("/me")
	public ResponseEntity<ApiResponse<Void>> updateProfile(Authentication authentication,
			@RequestBody UpdateProfileRequest updateRequest){
		UUID userUuid = getUserUuid(authentication);
		userService.updateProfile(userUuid, updateRequest);
		return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated", null));
		
	}
	
	 @PutMapping("/change-password")
	    public ResponseEntity<ApiResponse<Void>> changePassword(
	            Authentication authentication,
	            @RequestBody ChangePasswordRequest request) {

	        UUID userUuid = getUserUuid(authentication);
	        userService.changePassword(userUuid, request.oldPassword(), request.newPassword());
	        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed", null));
	    }
	 

	 private UUID getUserUuid(Authentication authentication) {
		    return UUID.fromString(authentication.getName());
		}


}
