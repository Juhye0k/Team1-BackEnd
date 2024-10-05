package com.club_board.club_board_server.config.jwt;
import com.club_board.club_board_server.domain.User;
import com.club_board.club_board_server.repository.AccessionRepository;
import com.club_board.club_board_server.repository.RefreshTokenRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;
    private final AccessionRepository accessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /*
    토큰 생성
     */
    public String generateAccessToken(User user, Duration expiredAt) {
        Date now=new Date();
        log.info("at 발급");
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    public String generateRefreshToken(User user,Duration expiredAt) {
        Date now=new Date();
        log.info("rt 발급");
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    private String makeToken(Date expiry, User user) {
        try{
            String base64SecretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes());
            Date now=new Date();
            List<Object[]> clubRoleList = accessionRepository.findClubIdAndRoleByUserId(user.getId());
            Map<String, String> result = new HashMap<>();
            for (Object[] entry : clubRoleList) {
                Long clubId = (Long) entry[0];  // 첫 번째 요소는 Long 타입의 clubId로 캐스팅
                String role = (String) entry[1]; // 두 번째 요소는 String 타입의 role로 캐스팅
                result.put(String.valueOf(clubId), "ROLE_"+role); // Long을 String으로 변환하여 추가
            }
            return Jwts.builder()
                    .setHeaderParam(Header.TYPE,Header.JWT_TYPE)  //헤더 타입
                    .setIssuer(jwtProperties.getIssuer())    //발급자
                    .setIssuedAt(now) // 발급 일시
                    .setExpiration(expiry)  // 만료 일시
                    .setSubject(user.getUsername()) // 유저이름
                    .claim("id",user.getId())  // 유저 ID
                    .claim("role", result)
                    .signWith(SignatureAlgorithm.HS256,jwtProperties.getSecretKey())
                    .compact();
        }
        catch(Exception e)
        {
            throw new BusinessException(ExceptionType.GENERATE_TOKEN_ERROR);
        }
    }

    public boolean validToken(String token)  //토큰이 유효한지 검사
    {
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

//    public Authentication getAuthentication(String token){
//        Claims claims =getClaims(token);
//        Map<String, String> clubRoles = claims.get("role", Map.class);
//        Set<SimpleGrantedAuthority> authorities = clubRoles.values().stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toSet());
//        return new UsernamePasswordAuthenticationToken(
//                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
//                token,
//                authorities
//        );
//    }

//    public boolean hasClubRole(Long clubId, String requiredRole) {
//        log.info("역할 확인");
//        // 현재 SecurityContext에 저장된 Authentication 객체를 가져옴
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String token = (String) authentication.getCredentials();  // JWT 토큰 추출
//
//        Claims claims = getClaims(token);  // JWT에서 클레임 추출
//        Map<String, String> clubRoles = claims.get("role", Map.class);  // 클레임에서 roles 정보 추출
//
//        // 해당 clubId에 대한 역할을 확인하고, requiredRole과 비교
//        return requiredRole.equals(clubRoles.get(String.valueOf(clubId)));
//    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
