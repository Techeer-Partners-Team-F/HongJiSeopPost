package techpart.webpost.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.repository.UserRepository;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JoinServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private JoinService joinService;

    @Test
    void 정상_회원가입(){
        String joinEmail = "admin123@naver.com";
        String password = "admin123";
        String username ="adminHong";

        JoinDto joinDto = new JoinDto(
            username,
            joinEmail,
            password,
            Role.ADMIN
        );

        User buildUser = User.builder()
            .name(username)
            .password(bCryptPasswordEncoder.encode(password))
            .email(joinEmail)
            .role(Role.ADMIN)
            .createdAt(LocalDateTime.now())
            .build();

        doReturn(buildUser).when(userRepository).save(any(User.class));
        doReturn(Optional.of(buildUser)).when(userRepository).findByEmail(joinEmail);

        //when
        ResUserDto resUserDto = joinService.join(joinDto);
        Optional<User> byEmail = userRepository.findByEmail(joinEmail);
        User foundUser = byEmail.get();

        //then
        assertThat(resUserDto.getEmail()).isEqualTo(joinEmail);
        assertThat(resUserDto.getRole()).isEqualTo(Role.ADMIN);
        assertThat(bCryptPasswordEncoder.matches(password,foundUser.getPassword())).isTrue();
        //verify
        //메소트 호출 횟수 검증
        verify(userRepository,times(1)).save(any(User.class));
    }
}