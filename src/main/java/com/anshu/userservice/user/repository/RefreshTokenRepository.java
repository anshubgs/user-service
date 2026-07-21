package com.anshu.userservice.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anshu.userservice.user.model.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUserUuid(UUID userUuid);

    void deleteByUserUuid(UUID userUuid);

    boolean existsByToken(String token);

}