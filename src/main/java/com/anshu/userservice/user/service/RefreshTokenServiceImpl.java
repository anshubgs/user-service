package com.anshu.userservice.user.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.anshu.userservice.common.exception.ResourceNotFoundException;
import com.anshu.userservice.common.exception.UnauthorizedException;
import com.anshu.userservice.config.JwtService;
import com.anshu.userservice.user.dto.JwtResponse;
import com.anshu.userservice.user.dto.RefreshTokenRequest;
import com.anshu.userservice.user.model.RefreshTokenEntity;
import com.anshu.userservice.user.model.UserAccount;
import com.anshu.userservice.user.repository.RefreshTokenRepository;
import com.anshu.userservice.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshExpiration;

    @Override
    public RefreshTokenEntity createRefreshToken(UserAccount user) {

        // Single active refresh token per user
        repository.deleteByUserUuid(user.getUuid());

        String token = jwtService.generateRefreshToken(user);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(token)
                .userUuid(user.getUuid())
                .expiryDate(
                        LocalDateTime.now()
                                .plus(Duration.ofMillis(refreshExpiration))
                )
                .revoked(false)
                .build();

        return repository.save(refreshToken);
    }

    @Override
    public RefreshTokenEntity verifyToken(String token) {

        RefreshTokenEntity refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh token not found"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        return refreshToken;
    }

    @Override
    public void revokeToken(UUID userUuid) {

        repository.deleteByUserUuid(userUuid);
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest request) {

        // Validate refresh token
        RefreshTokenEntity refreshToken =
                verifyToken(request.refreshToken());

        // Load user
        UserAccount user = userRepository.findByUuid(refreshToken.getUserUuid())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Generate new access token
        String accessToken = jwtService.generateToken(
                user.getUuid(),
                user.getHouseUuid(),
                user.getRole()
        );

        // Return response
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

}