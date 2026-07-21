package com.anshu.userservice.user.service;

import java.util.UUID;

import com.anshu.userservice.user.dto.JwtResponse;
import com.anshu.userservice.user.dto.RefreshTokenRequest;
import com.anshu.userservice.user.model.RefreshTokenEntity;
import com.anshu.userservice.user.model.UserAccount;

public interface RefreshTokenService {

    /**
     * Creates and stores a new refresh token for the user.
     */
    RefreshTokenEntity createRefreshToken(UserAccount user);

    /**
     * Validates refresh token from database and JWT.
     */
    RefreshTokenEntity verifyToken(String token);

    /**
     * Deletes/Revoke refresh token for the user.
     */
    void revokeToken(UUID userUuid);

    /**
     * Generates a new access token using a valid refresh token.
     */
    JwtResponse refreshToken(RefreshTokenRequest request);

}