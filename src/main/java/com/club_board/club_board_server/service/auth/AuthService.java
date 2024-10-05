package com.club_board.club_board_server.service.auth;
import com.club_board.club_board_server.config.jwt.TokenProvider;
import com.club_board.club_board_server.domain.CustomUserDetails;
import com.club_board.club_board_server.domain.RefreshToken;
import com.club_board.club_board_server.domain.User;
import com.club_board.club_board_server.dto.auth.ResetPasswordRequest;
import com.club_board.club_board_server.dto.auth.UserLoginRequest;
import com.club_board.club_board_server.dto.auth.UserLoginResponse;
import com.club_board.club_board_server.repository.RefreshTokenRepository;
import com.club_board.club_board_server.repository.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private static final String UPPER_CASE="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE="abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS="0123456789";
    private static final String SPECIAL_CHARACTERS="@!#$%^&*()_+";
    private final TemporaryEmailService temporaryEmailService;
    private static final String ALL_CHARACTERS=UPPER_CASE + LOWER_CASE +DIGITS+SPECIAL_CHARACTERS;
    private static final int MIN_PASSWORD_LENGTH=10;
    private static final int MAX_PASSWORD_LENGTH=20;
    private static final SecureRandom RANDOM=new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    /*
    로그인 메소드
     */
    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest)
    {
        try{
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequest.getUsername(),
                            userLoginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
            User user=userDetails.getUser();
            String accessToken=tokenProvider.generateAccessToken(user, Duration.ofHours(1));
            String refreshToken = tokenProvider.generateRefreshToken(user, Duration.ofDays(7));
            RefreshToken refreshTokenEntity = refreshTokenRepository.findById(user.getId())
                    .map(existingToken -> {
                        existingToken.update(refreshToken); // 기존 토큰을 업데이트
                        return existingToken;
                    })
                    .orElseGet(() -> {
                        // 값이 없을 경우 새로운 RefreshToken 생성
                        return new RefreshToken(user.getId(), refreshToken);
                    });
            refreshTokenRepository.save(refreshTokenEntity);
            String message = "로그인 성공";
            return new UserLoginResponse(message,accessToken, refreshToken);
        }
        catch (Exception e)
        {
            throw new BusinessException(ExceptionType.INVALID_LOGIN);
        }
    }

    /*
    임시 비밀번호 찾기 메소드
     */
    @Transactional
    public void forgotPassword(ResetPasswordRequest resetPasswordRequest)
    {
        String username=resetPasswordRequest.getUsername();
        String name=resetPasswordRequest.getName();
        String studentId= resetPasswordRequest.getStudent_id();
        // 존재하는 유저인지 확인
        User userObj=userRepository.findByUsername(username).orElseThrow(
                ()->new BusinessException(ExceptionType.FIND_PASSWORD_ERROR));
        if(userObj.getUsername().equals(username) && userObj.getName().equals(name) && userObj.getStudent_id().equals(studentId))
        {
            String newPassword=generateTemporaryPassword();
            temporaryEmailService.sendPasswordMail(username, newPassword);
            String encodedPassword=passwordEncoder.encode(newPassword);
            userObj.issuePassword(encodedPassword);
            userRepository.save(userObj);
        }
        else
        {
            throw new BusinessException(ExceptionType.FIND_PASSWORD_ERROR);
        }
    }

    /*
    임시 비밀번호 생성 로직
     */
    public static String generateTemporaryPassword(){
        try{
            int passwordLength=RANDOM.nextInt(MAX_PASSWORD_LENGTH-MIN_PASSWORD_LENGTH+1)+MIN_PASSWORD_LENGTH;
            StringBuilder password=new StringBuilder(passwordLength);
            password.append(UPPER_CASE.charAt(RANDOM.nextInt(UPPER_CASE.length())));
            password.append(LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())));
            password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
            password.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));
            for(int i=4;i<passwordLength;i++){
                password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
            }
            return shuffleString(password.toString());
        }
        catch (Exception e)
        {
            throw new BusinessException(ExceptionType.TEMPORARY_PASSWORD_ERROR);
        }
    }
    /*
    Refresh-Token 검증
     */
    public String isValidRefreshToken(String refreshToken){
        try{
            refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                    ()->new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN));
            String username=tokenProvider.getClaims(refreshToken).getSubject();
            CustomUserDetails userDetails=(CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
            return tokenProvider.generateAccessToken(userDetails.getUser(),Duration.ofHours(1));
        }
        catch (Exception e)
        {
            throw new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN);
        }
    }

    private static String shuffleString(String input){
        StringBuilder shuffled=new StringBuilder(input.length());
        char[] characters=input.toCharArray();
        for(int i=characters.length;i>0;i--){
            int randomIndex=RANDOM.nextInt(i);
            shuffled.append(characters[randomIndex]);
            characters[randomIndex]=characters[i-1];
        }
        return shuffled.toString();
    }
}
