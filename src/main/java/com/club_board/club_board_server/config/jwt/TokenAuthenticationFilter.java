package com.club_board.club_board_server.config.jwt;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.auth.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private static final String TOKEN_PREFIX = "Bearer ";
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(request);
        if (accessToken != null) {
            boolean isAccessTokenExpired = tokenProvider.validToken(accessToken);
            if (!isAccessTokenExpired) {
                authenticateWithToken(accessToken);
            }else{
                throw new BusinessException(ExceptionType.INVALID_ACCESS_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateWithToken(String token) {
        String username = tokenProvider.getClaims(token).getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getAccessToken(HttpServletRequest request) {
        String bearerToken=request.getHeader("authorization");
        log.info("AT={}",bearerToken);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
