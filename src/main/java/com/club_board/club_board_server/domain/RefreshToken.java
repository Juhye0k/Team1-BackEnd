package com.club_board.club_board_server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",updatable=false)
    private Long id;

    @Column(name="user_id",nullable=false,unique=true)
    private Long userId;

    @Column(name="refresh_token",nullable=false,length = 1024)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
    public void update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
