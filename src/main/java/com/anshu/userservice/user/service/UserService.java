package com.anshu.userservice.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.anshu.userservice.user.dto.AuthResponse;
import com.anshu.userservice.user.dto.CreateMemberRequest;
import com.anshu.userservice.user.dto.CreateMemberResponse;
import com.anshu.userservice.user.dto.LoginRequest;
import com.anshu.userservice.user.dto.RegisterRequest;
import com.anshu.userservice.user.dto.UpdateProfileRequest;
import com.anshu.userservice.user.dto.UserResponse;
import com.anshu.userservice.user.model.UserAccount;

import jakarta.validation.Valid;

//@Service
public interface UserService {

	void register(@Valid RegisterRequest request);

	AuthResponse login(@Valid LoginRequest request);

	UserResponse getProfile(UUID userUuid);

	void updateProfile(UUID userUuid, UpdateProfileRequest request);

	void changePassword(UUID userUuid, String oldPassword, String newPassword);

	CreateMemberResponse createMember(@Valid CreateMemberRequest request, UUID adminUuid);

}
