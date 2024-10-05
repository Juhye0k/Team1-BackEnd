package com.club_board.club_board_server.controller.auth;
import com.club_board.club_board_server.dto.auth.ResetPasswordRequest;
import com.club_board.club_board_server.dto.auth.UserLoginRequest;
import com.club_board.club_board_server.dto.auth.UserLoginResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthService authService;

    @PostMapping()
    public ResponseEntity<ResponseBody<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest loginRequest){
        UserLoginResponse userLoginResponse= authService.login(loginRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(userLoginResponse));
    }
    @PostMapping("/password")
    public ResponseEntity<ResponseBody<String>> findPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.forgotPassword(resetPasswordRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("임시 비밀번호 생성 완료"));
    }
}
