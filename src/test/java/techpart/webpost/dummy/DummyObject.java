package techpart.webpost.dummy;

import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.constant.Role;

public class DummyObject {
    public static final String RAW_PASSWORD = "1234";
    public static final String TEST_EMAIL = "admin123@naver.com";
    public static final String TEST_PASSWORD = "admin123";
    public static final String TEST_USERNAME ="adminHong";
    public static final String TEST_CONTENT = "test content";
    public static final String TEST_TITLE = "test title";

    public static User newUserForTest(String email){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return User.builder()
            .email(email)
            .name(TEST_USERNAME)
            .role(Role.USER)
            .password(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Post newPostForTest(User user, PostDto postDto){
        return new Post(user, postDto.getTitle(), postDto.getContent());
    }
}
