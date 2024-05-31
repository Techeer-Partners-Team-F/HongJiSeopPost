package techpart.webpost.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import techpart.webpost.config.auth.LoginUser;
import techpart.webpost.domain.User;
import techpart.webpost.global.constant.Role;

@Slf4j
@Component
public class JwtProcess {

    private final String secret;

    public JwtProcess(@Value("${spring.jwt.secret}")String secret) {
        this.secret = secret;
    }
    // 토큰 생성
    public String create(LoginUser loginUser) {
        String jwtToken = JWT.create()
            .withSubject("bank")
            .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME)) //만료날짜 = 현재시간 + 만료기간
            .withClaim("id", loginUser.getUser().getId())
            .withClaim("role", loginUser.getUser().getRole().name())
            .sign(Algorithm.HMAC512(secret));
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    // 토큰 검증
    // (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정)
    public LoginUser verify(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        User user = User.builder().id(id).role(Role.valueOf(role)).build();
        return new LoginUser(user);
    }
}