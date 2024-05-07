package techpart.webpost.global.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.security.jwt.JWTFilter;
import techpart.webpost.global.security.jwt.JWTUtil;
import techpart.webpost.global.security.jwt.LoginFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        log.info("SecurityConfig.filterChain1");

        //csrf disable
        //jwt방식은 session을 stateless 상태로 두기 때문에
        //csrf에 대한 염려가 적다.
        http
                .csrf((auth)-> auth.disable());

        //Form login disable
        http
                .formLogin((auth)-> auth.disable());

        //http basic disable
        http
                .httpBasic((auth)-> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth)-> auth
                        .requestMatchers("/","/api/login","/api/join").permitAll()
                        .requestMatchers("/api/post").hasAnyRole(Role.ADMIN.toString(),Role.USER.toString())
                        .requestMatchers("/admin").hasRole(Role.ADMIN.toString())
                        .anyRequest().authenticated());

        //세션 stateless 상태로 관리하도록 설정
        http
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        log.info("SecurityConfig.filterChain1");

        //로그인 필터 앞에 jwt필터 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        log.info("SecurityConfig.filterChain2");

        //로그인 필터 추가
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                    objectMapper), UsernamePasswordAuthenticationFilter.class);
        log.info("SecurityConfig.filterChain3");

        return http.build();
    }
}
