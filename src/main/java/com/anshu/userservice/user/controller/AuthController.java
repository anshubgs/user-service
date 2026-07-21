package com.anshu.userservice.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.anshu.userservice.common.response.ApiResponse;
import com.anshu.userservice.user.dto.AuthResponse;
import com.anshu.userservice.user.dto.JwtResponse;
import com.anshu.userservice.user.dto.LoginRequest;
import com.anshu.userservice.user.dto.RefreshTokenRequest;
import com.anshu.userservice.user.dto.RegisterRequest;
import com.anshu.userservice.user.service.RefreshTokenService;
import com.anshu.userservice.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final UserService userService;
	private final RefreshTokenService refreshTokenService;
	
	@PostMapping("/register")
	public ResponseEntity <ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request){
		userService.register(request);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>(true, "Registration successful", null));
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request){
		AuthResponse response = userService.login(request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(
	        @Valid @RequestBody RefreshTokenRequest request){

	    JwtResponse response = refreshTokenService.refreshToken(request);

	    return ResponseEntity.ok(
	            new ApiResponse<>(true,
	                    "Token refreshed successfully",
	                    response));
	}
	
    
}
