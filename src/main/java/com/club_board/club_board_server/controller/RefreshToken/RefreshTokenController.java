package com.club_board.club_board_server.controller.RefreshToken;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RequiredArgsConstructor
@RestController
public class RefreshTokenController {

    private final AuthService authService;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String newAccessToken=authService.isValidRefreshToken(refreshToken);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(newAccessToken));
    }
}
