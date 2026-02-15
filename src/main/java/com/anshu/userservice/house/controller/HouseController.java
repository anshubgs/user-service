package com.anshu.userservice.house.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.anshu.userservice.common.response.ApiResponse;
import com.anshu.userservice.house.dto.CreateHouseRequest;
import com.anshu.userservice.house.dto.HouseResponse;
import com.anshu.userservice.house.service.HouseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @PostMapping
    public ResponseEntity<ApiResponse<HouseResponse>> createHouse(
            Authentication authentication,
            @Valid @RequestBody CreateHouseRequest request) {

        UUID userUuid = UUID.fromString(authentication.getName());

        HouseResponse response = houseService.createHouse(userUuid, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "House created successfully", response)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<HouseResponse>> getMyHouse(Authentication authentication) {

        UUID userUuid = UUID.fromString(authentication.getName());

        HouseResponse response = houseService.getMyHouse(userUuid);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "House fetched successfully", response)
        );
    }
}
