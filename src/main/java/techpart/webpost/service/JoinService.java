package techpart.webpost.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.global.exception.ErrorCode;
import techpart.webpost.repository.UserRepository;
import techpart.webpost.validation.UserValidation;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserValidation userValidation;

    public ResUserDto join(JoinDto joinDto){
        log.info("joinService start");
        String username = joinDto.getName();
        String userEmail = joinDto.getEmail();
        String password = joinDto.getPassword();
        Role role = joinDto.getRole();

        boolean isExist = userRepository.existsByEmail(userEmail);
        userValidation.checkEmailIsDupl(isExist);

        User newUser = User.builder()
            .name(username)
            .email(userEmail)
            .role(role)
            .password(bCryptPasswordEncoder.encode(password))
            .createdAt(LocalDateTime.now())
            .modifiedAt(null)
            .build();

        userRepository.save(newUser);
        log.info(userEmail);

        return new ResUserDto(
            username,
            userEmail,
            role
        );
    }


}
