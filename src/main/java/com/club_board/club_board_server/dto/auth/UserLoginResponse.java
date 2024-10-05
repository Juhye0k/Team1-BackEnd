package com.club_board.club_board_server.dto.auth;

import lombok.Getter;

@Getter
public class UserLoginResponse {

    private String message;
    private String accessToken;
    private String refreshToken;

    public UserLoginResponse(String message ,String accessToken, String refreshToken) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
