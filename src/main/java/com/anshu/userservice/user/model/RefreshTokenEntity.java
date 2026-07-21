package com.anshu.userservice.user.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "refresh_token",
    schema = "users",
    indexes = {
        @Index(name = "idx_refresh_user", columnList = "user_uuid")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "token",
            nullable = false,
            unique = true,
            length = 500
    )
    private String token;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Builder.Default
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

}