package com.anshu.userservice.house.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.anshu.userservice.common.exception.BadRequestException;
import com.anshu.userservice.common.exception.ResourceNotFoundException;
import com.anshu.userservice.event.HouseCreatedEvent;
import com.anshu.userservice.event.HouseEventPublisher;
import com.anshu.userservice.house.dto.CreateHouseRequest;
import com.anshu.userservice.house.dto.HouseResponse;
import com.anshu.userservice.house.model.House;
import com.anshu.userservice.house.repository.HouseRepository;
import com.anshu.userservice.user.model.UserAccount;
import com.anshu.userservice.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final HouseEventPublisher houseEventPublisher;

    @Override
    public HouseResponse createHouse(UUID userUuid, CreateHouseRequest request) {

        UserAccount user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getHouseUuid() != null) {
            throw new BadRequestException("User already has a house");
        }

        House house = House.builder()
                .name(request.name())
                .ownerUuid(userUuid)
                .build();

        houseRepository.save(house);

        // Update user with house UUID
        user.setHouseUuid(house.getUuid());
        
        // ✅ Publish Event
        HouseCreatedEvent event = HouseCreatedEvent.builder()
                .houseUuid(house.getUuid())
                .ownerUuid(userUuid)
                .name(house.getName())
                .build();

        houseEventPublisher.publish(event);


        return HouseResponse.builder()
                .uuid(house.getUuid())
                .name(house.getName())
                .ownerUuid(house.getOwnerUuid())
                .build();
    }

    @Override
    public HouseResponse getMyHouse(UUID userUuid) {

        House house = houseRepository.findByOwnerUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("House not found"));

        return HouseResponse.builder()
                .uuid(house.getUuid())
                .name(house.getName())
                .ownerUuid(house.getOwnerUuid())
                .build();
    }
}
