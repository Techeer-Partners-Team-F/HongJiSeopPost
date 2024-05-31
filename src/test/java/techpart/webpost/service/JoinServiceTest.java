package techpart.webpost.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.predicate;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doThrow;

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
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.global.exception.ErrorCode;
import techpart.webpost.repository.UserRepository;
import techpart.webpost.validation.UserValidation;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JoinServiceTest {

    private static final String JOIN_EMAIL = "admin123@naver.com";
    private static final String PASSWORD = "admin123";
    private static final String USERNAME ="adminHong";

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserValidation userValidation;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private JoinService joinService;

    @Test
    void 정상_회원가입(){
        JoinDto joinDto = new JoinDto(
            USERNAME,
            JOIN_EMAIL,
            PASSWORD,
            Role.ADMIN
        );

        User buildUser = new User(USERNAME,
            JOIN_EMAIL,
            PASSWORD,
            Role.ADMIN,
            bCryptPasswordEncoder);

        doReturn(buildUser).when(userRepository).save(any(User.class));
        doReturn(Optional.of(buildUser)).when(userRepository).findByEmail(JOIN_EMAIL);

        //when
        ResUserDto resUserDto = joinService.join(joinDto);
        Optional<User> byEmail = userRepository.findByEmail(JOIN_EMAIL);
        User foundUser = byEmail.get();

        //then
        assertThat(resUserDto.getEmail()).isEqualTo(JOIN_EMAIL);
        assertThat(resUserDto.getRole()).isEqualTo(Role.ADMIN);
        assertThat(bCryptPasswordEncoder.matches(PASSWORD,foundUser.getPassword())).isTrue();
        //verify
        //메소트 호출 횟수 검증
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void 비정상_회원가입_중복이메일회원() {

        User firstUser = new User(USERNAME,
            JOIN_EMAIL,
            PASSWORD,
            Role.ADMIN,
            bCryptPasswordEncoder
        );

        JoinDto joinDto = new JoinDto(
            USERNAME,
            JOIN_EMAIL,
            PASSWORD,
            Role.ADMIN
        );

        doReturn(firstUser).when(userRepository).save(firstUser);
        doReturn(true).when(userRepository).existsByEmail(JOIN_EMAIL);
        doThrow(BusinessException.class).when(userValidation).checkEmailIsDupl(true);

        //when
        userRepository.save(firstUser);

        //then
        assertThatThrownBy(() -> joinService.join(joinDto))
            .isInstanceOf(BusinessException.class);

        verify(userValidation,times(1)).checkEmailIsDupl(true);
    }
}