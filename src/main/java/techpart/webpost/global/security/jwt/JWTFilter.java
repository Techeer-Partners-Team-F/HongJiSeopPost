package techpart.webpost.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import techpart.webpost.domain.User;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.security.CustomUserDetails;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    //Authorization 헤더 검증 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        //authorization에 대한 문제가 있을 시 종료
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.info("there is no any token");

            //request, response를 다음 필터로 넘겨준다.
            filterChain.doFilter(request, response);

            //종료
            return;
        }

        log.info("authorization start");

        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        //true면 소멸된거다.
        if(jwtUtil.isExpired(token)){
            filterChain.doFilter(request, response);

            return;
        }

        log.info("expired not yet");

        //토큰에서 username과 role 얻기
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        //임시 user엔티티 생성
        //password를 포함한 나머지들은 토큰 payload에 넣지 않았으므로
        // DB에서 매번뽑아오는 작업보다는 아무 임시 string을 부여한다.
        User userForAuth = User.builder()
            .name("tmpname")
            .email(email)
            .password("tmppassword")
            .role(Role.from(role))
            .createdAt(LocalDateTime.now())
            .modifiedAt(null)
            .build();

        //UserDetails 회원 정보 객체

        CustomUserDetails customUserDetails = new CustomUserDetails(userForAuth);

        //인증 토큰 생성
        Authentication usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}
