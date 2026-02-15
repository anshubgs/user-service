package com.anshu.userservice.house.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anshu.userservice.house.model.House;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {

    Optional<House> findByUuid(UUID uuid);

    Optional<House> findByOwnerUuid(UUID ownerUuid);
}
