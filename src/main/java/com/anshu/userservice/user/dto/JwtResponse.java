package com.anshu.userservice.user.dto;

import lombok.Builder;

@Builder
public record JwtResponse(

        String accessToken,

        String refreshToken

) {}