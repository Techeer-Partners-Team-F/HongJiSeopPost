package techpart.webpost.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import techpart.webpost.config.auth.LoginUser;

/*
 * 모든 주소에서 동작함 (토큰 검증)
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtProcess jwtProcess;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
        JwtProcess jwtProcess) {
        super(authenticationManager);
        this.jwtProcess = jwtProcess;
    }

    // JWT 토큰 헤더를 추가하지 않아도 해당 필터는 통과는 할 수 있지만, 결국 시큐리티단에서 세션 값 검증에 실패함.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        if (isHeaderVerify(request, response)) {
            // 토큰이 존재함
            log.info("디버그 : 토큰이 존재함");

            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
            log.info("token : "+token);
            LoginUser loginUser = jwtProcess.verify(token);
            log.info("디버그 : 토큰이 검증이 완료됨");
            log.info("loginUser.getUsername() (이메일) : "+loginUser.getUsername());
            log.info("loginUser.getPassword() : "+loginUser.getPassword());
            log.info(loginUser.getUser().getName());
            log.info(loginUser.getUser().getRole().getName());

            // 임시 세션 (첫 인자 값 : UserDetails 타입 or username)
            // 근데 지금 username가 null값이고 어짜피 verify를 했기때문에 어떤 값이 들어가든 상관없다. 임시 세션 생성자체가 목표기 때문
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null,
                loginUser.getAuthorities()); // id, role 만 존재

            //강제 로그인
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("디버그 : 임시 세션이 생성됨");
        }
        chain.doFilter(request, response);
    }

    //토큰이 존재하는지 체크
    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(JwtVO.HEADER);
        if (header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)) {
            return false;
        } else {
            return true;
        }
    }
}
