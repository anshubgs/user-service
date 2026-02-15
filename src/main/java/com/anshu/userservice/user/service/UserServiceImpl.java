package com.anshu.userservice.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anshu.userservice.common.exception.BadRequestException;
import com.anshu.userservice.common.exception.ResourceNotFoundException;
import com.anshu.userservice.common.exception.UnauthorizedException;
import com.anshu.userservice.config.JwtService;
import com.anshu.userservice.user.dto.AuthResponse;
import com.anshu.userservice.user.dto.LoginRequest;
import com.anshu.userservice.user.dto.RegisterRequest;
import com.anshu.userservice.user.dto.UpdateProfileRequest;
import com.anshu.userservice.user.dto.UserResponse;
import com.anshu.userservice.user.model.UserAccount;
import com.anshu.userservice.user.model.UserStatus;
import com.anshu.userservice.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(@Valid RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        UserAccount account = UserAccount.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(account);
    }

    @Override
    public AuthResponse login(@Valid LoginRequest request) {

        UserAccount user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Account is blocked");
        }

        boolean requiresHouseSetup = (user.getHouseUuid() == null);

        String token = jwtService.generateToken(
                user.getUuid(),
                user.getHouseUuid()
        );

        return AuthResponse.builder()
                .accessToken(token)
                .requiresHouseSetup(requiresHouseSetup)
                .build();
    }

    @Override
    public UserResponse getProfile(UUID userUuid) {

        UserAccount user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .uuid(user.getUuid())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .houseUuid(user.getHouseUuid())
                .build();
    }

    @Override
    public void updateProfile(UUID userUuid, UpdateProfileRequest request) {

        UserAccount user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());
    }

    @Override
    public void changePassword(UUID userUuid, String oldPassword, String newPassword) {

        UserAccount user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (oldPassword.equals(newPassword)) {
            throw new BadRequestException("New password must be different from old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
