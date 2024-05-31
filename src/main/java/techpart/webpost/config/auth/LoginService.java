package techpart.webpost.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import techpart.webpost.domain.User;
import techpart.webpost.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    // 시큐리티로 로그인이 될때, 시큐리티가 loadUserByUsername() 실행해서 username을 체크
    // 없으면 오류
    // 있으면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 새로 생성
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userPS = userRepository.findByEmail(email).orElseThrow(
            () -> new InternalAuthenticationServiceException("인증 실패")); // 테스트 클래스에 설명 첨부
        return new LoginUser(userPS);
    }
}