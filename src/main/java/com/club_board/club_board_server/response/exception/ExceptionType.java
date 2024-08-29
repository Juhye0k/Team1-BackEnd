package com.club_board.club_board_server.response.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    //common
    UNEXPECTED_SERVER_ERROR(INTERNAL_SERVER_ERROR,"C001","예상치 못한 에러 발생"),
    BINDING_ERROR(BAD_REQUEST,"C002","바인딩시 에러 발생"),
    ESSENTIAL_FIELD_MISSING_ERROR(NO_CONTENT , "C003","필수적인 필드 부재"),

    //User
    USER_ALREADY_EXIST(HttpStatus.CONFLICT,"U001","이미 존재하는 회원입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED,"U002","아이디 또는 비밀번호가 일치하지 않습니다."),
    NOT_CORRECT_PASSWORD(UNAUTHORIZED,"U003","비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    INVALID_EMAIL_FORMAT(BAD_REQUEST,"U004","이메일 형식이 올바르지 않습니다."),
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR,"U005","알수없는 오류로 인해 이메일 전송에 실패하였습니다."),
    INVALID_EMAIL_CODE(UNAUTHORIZED,"U006","인증번호가 일치하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
