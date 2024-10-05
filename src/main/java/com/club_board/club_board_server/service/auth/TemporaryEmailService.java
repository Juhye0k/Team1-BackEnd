package com.club_board.club_board_server.service.auth;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class TemporaryEmailService {
    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "cowngur5460@gmail.com";
    @Async
    public void sendPasswordMail(String username, String password) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, username);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 임시 비밀번호입니다.." + "</h3>";
            body += "<h1>" + password + "</h1>";
            body += "<h3>" + "보안을 위해 꼭 비밀번호 변경 해주세요!." + "</h3>";
            message.setText(body,"UTF-8", "html");
            javaMailSender.send(message);
            log.info("임시 비밀번호 전송 완료");
        } catch (MessagingException e) {
            throw new BusinessException(ExceptionType.EMAIL_SEND_ERROR);
        }
    }
}
