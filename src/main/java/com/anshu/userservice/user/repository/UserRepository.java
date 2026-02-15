package com.anshu.userservice.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anshu.userservice.user.model.UserAccount;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long>{

	boolean existsByEmail(@Email @NotBlank String email);

	//void save(UserAccount account);

	Optional<UserAccount> findByEmail( String email);

	Optional<UserAccount> findByUuid(UUID userUuid);

}
