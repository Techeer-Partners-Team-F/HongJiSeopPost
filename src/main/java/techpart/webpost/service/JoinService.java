package techpart.webpost.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ResUserDto join(JoinDto joinDto){
        log.info("joinService start");
        String username = joinDto.getName();
        String userEmail = joinDto.getEmail();
        String password = joinDto.getPassword();
        Role role = joinDto.getRole();

        Boolean isExist = userRepository.existsByEmail(userEmail);
        if(isExist) return null;

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
