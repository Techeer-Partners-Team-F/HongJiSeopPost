package techpart.webpost.global.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import techpart.webpost.domain.User;
import techpart.webpost.global.constant.Role;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyData {

    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDatabase(){
        User user = new User("adminHong", "admin@naver.com", "admin12", Role.ADMIN,
            new BCryptPasswordEncoder());
        userRepository.save(user);
    }
}
