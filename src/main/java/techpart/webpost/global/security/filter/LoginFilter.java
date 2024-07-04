package techpart.webpost.global.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import techpart.webpost.domain.Refresh;
import techpart.webpost.global.security.CustomUserDetails;
import techpart.webpost.global.security.filter.jwt.JWTUtil;
import techpart.webpost.repository.RefreshRepository;

@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/login"; // "/login"으로 오는 요청을 처리
    private static final String HTTP_METHOD_POST = "POST"; // 로그인 HTTP 메소드는 POST
    private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리
    private static final String USERNAME_KEY = "email"; // 회원 로그인 시 이메일 요청 JSON Key : "email"
    private static final String PASSWORD_KEY = "password"; // 회원 로그인 시 비밀번호 요청 JSon Key : "password"
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
        new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL,
            HTTP_METHOD_POST); // "/login" + POST로 온 요청에 매칭된다.

    private final AuthenticationManager authenticationManeger;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, ObjectMapper objectMapper,
        RefreshRepository refreshRepository) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.authenticationManeger = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.refreshRepository = refreshRepository;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        log.info("attemptAuthentication");
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException(
                "Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = null;
        try {
            messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> usernamePasswordMap = null;
        try {
            usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        String email = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);
        log.info("attemptAuthentication user email = {}",email);
        log.info("attemptAuthentication user password = {}",password);

        // 스프링시큐리티에서 email과 password를 검증하기 위해
        // token에 담는다.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            email, password);
        log.info("UsernamePasswordAuthenticationToken");

        //token을 authenticationManeger 로 검증
        return authenticationManeger.authenticate(usernamePasswordAuthenticationToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain,
        Authentication authentication){

        log.info("successfulAuthentication");
        //유저정보
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", email, role, 600000L); //10분
        String refresh = jwtUtil.createJwt("refresh", email, role, 86400000L); //24시간

        //refresh 토큰 저장
        addRefreshEntity(email, refresh, 86400000L);

        //응답 설정
        //헤더에는 access 토큰
        //쿠키에는 refresh 토큰
        //프론트 단에서 헤더의 access 토큰을 가져와서 로컬 스토리지에 저장 후 사용하면 된다.
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed) throws IOException, ServletException {

        log.info("unsuccessfulAuthentication");
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); //생명주기
        //cookie.setSecure(true); //https 통신 진행시 설정
        //cookie.setPath("/"); // 쿠키 적용 범위 설정
        cookie.setHttpOnly(true); //httpOnly설정 -> js접근 방지

        return cookie;
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = Refresh.builder()
            .email(email)
            .refresh(refresh)
            .expiration(date.toString())
            .build();
        refreshRepository.save(refreshEntity);
    }

}
