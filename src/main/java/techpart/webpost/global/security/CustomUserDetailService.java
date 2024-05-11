package techpart.webpost.global.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import techpart.webpost.domain.User;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        log.info("loadUserByUsername userOptional.isEmpty = {}",userOptional.isEmpty());
        if(userOptional.isPresent()) {
            log.info("loadUserByUsername userOptional.get().getEmail() = {}", userOptional.get().getEmail());
            log.info("loadUserByUsername userOptional.get().getRole() = {}", userOptional.get().getRole());
            return new CustomUserDetails(userOptional.get());
        }

        return null;
    }
}
