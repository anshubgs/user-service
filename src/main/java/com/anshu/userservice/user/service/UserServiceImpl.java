package com.anshu.userservice.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anshu.userservice.common.exception.BadRequestException;
import com.anshu.userservice.common.exception.ResourceNotFoundException;
import com.anshu.userservice.common.exception.UnauthorizedException;
import com.anshu.userservice.config.JwtService;
import com.anshu.userservice.event.UserEventPublisher;
import com.anshu.userservice.event.UserSyncedEvent;
import com.anshu.userservice.user.dto.AuthResponse;
import com.anshu.userservice.user.dto.CreateMemberRequest;
import com.anshu.userservice.user.dto.CreateMemberResponse;
import com.anshu.userservice.user.dto.LoginRequest;
import com.anshu.userservice.user.dto.RegisterRequest;
import com.anshu.userservice.user.dto.UpdateProfileRequest;
import com.anshu.userservice.user.dto.UserResponse;
import com.anshu.userservice.user.model.RefreshTokenEntity;
import com.anshu.userservice.user.model.UserAccount;
import com.anshu.userservice.user.model.UserRole;
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
    private final UserEventPublisher userEventPublisher;
    private final RefreshTokenService refreshTokenService;

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
        publishUserEvent(account, "USER_CREATED");
    }

    private void publishUserEvent(UserAccount user, String eventType) {

        UserSyncedEvent event = UserSyncedEvent.builder()
        		.eventType(eventType)
                .userUuid(user.getUuid())
                .houseUuid(user.getHouseUuid())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();

        userEventPublisher.publish(event);
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

//        String token = jwtService.generateToken(
//                user.getUuid(),
//                user.getHouseUuid(),
//                user.getRole()
//        );
        String accessToken = jwtService.generateToken(
                user.getUuid(),
                user.getHouseUuid(),
                user.getRole());

        RefreshTokenEntity refreshToken =
                refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .requiresHouseSetup(requiresHouseSetup)
                .build();

//        return AuthResponse.builder()
//                .accessToken(token)
//                .requiresHouseSetup(requiresHouseSetup)
//                .build();
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

	@Override
	public CreateMemberResponse createMember(@Valid CreateMemberRequest request, UUID adminUuid) {
		
		UserAccount admin = userRepository.findByUuid(adminUuid)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
		
		if(admin.getRole() != UserRole.ADMIN) {
			 throw new UnauthorizedException("Only ADMIN can create members");
		}
		
		if(admin.getHouseUuid() == null) {
			  throw new BadRequestException("Admin has no house assigned");
		}
		
		if (userRepository.existsByEmail(request.email())) {
	        throw new BadRequestException("Email already registered");
	    }

		 UserAccount member = UserAccount.builder()
				 .fullName(request.fullName())
		            .email(request.email())
		            .phoneNumber(request.phoneNumber())
		            .password(passwordEncoder.encode(request.password()))
		            .role(UserRole.MEMBER)
		            .houseUuid(admin.getHouseUuid())
		            .status(UserStatus.ACTIVE)
		            .build();
		 
		  userRepository.save(member);

		    // 🔥 Publish Sync Event
		    publishUserEvent(member,"USER_CREATED");

		    return CreateMemberResponse.builder()
		            .userUuid(member.getUuid())
		            .fullName(member.getFullName())
		            .email(member.getEmail())
		            .role(member.getRole().name())
		            .houseUuid(member.getHouseUuid())
		            .build();
				
	}
}
