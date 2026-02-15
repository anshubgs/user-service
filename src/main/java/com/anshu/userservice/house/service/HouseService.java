package com.anshu.userservice.house.service;

import java.util.UUID;

import com.anshu.userservice.house.dto.CreateHouseRequest;
import com.anshu.userservice.house.dto.HouseResponse;

public interface HouseService {

    HouseResponse createHouse(UUID userUuid, CreateHouseRequest request);

    HouseResponse getMyHouse(UUID userUuid);
}
