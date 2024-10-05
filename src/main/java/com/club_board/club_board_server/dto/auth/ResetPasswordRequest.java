package com.club_board.club_board_server.dto.auth;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String username;
    private String name;
    private String student_id;
}
