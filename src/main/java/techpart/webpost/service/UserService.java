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
import techpart.webpost.dto.response.ResUserInfoDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.global.exception.ErrorCode;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final UserValidation userValidation;

    public ResUserDto join(JoinDto joinDto){
        log.info("joinService start");
        String username = joinDto.getName();
        String userEmail = joinDto.getEmail();
        String password = joinDto.getPassword();
        Role role = joinDto.getRole();

        boolean isExist = userRepository.existsByEmail(userEmail);
        if(isExist){
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }

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

    public void updateUserInfo(JoinDto joinDto,Long userId){
        User user = userRepository.findById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
        user.update(joinDto);
    }

    public ResUserInfoDto getUserInfo(JoinDto joinDto,Long userId){
        User user = userRepository.findById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
        return new ResUserInfoDto(joinDto);
    }

}
