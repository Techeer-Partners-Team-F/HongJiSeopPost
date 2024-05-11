package techpart.webpost.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.repository.PostRepository;
import techpart.webpost.repository.ReplyRepository;
import techpart.webpost.repository.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private static final String EMAIL = "admin123@naver.com";
    private static final String PASSWORD = "admin123";
    private static final String USERNAME ="adminHong";
    private static final String TEST_CONTENT = "test content";
    private static final String TEST_TITLE = "test title";

    private static final User TEST_USER = new User(USERNAME, EMAIL,PASSWORD, Role.ROLE_ADMIN,new BCryptPasswordEncoder());
    private Post testPost;

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void reset(){

        testPost = new Post(TEST_USER, TEST_TITLE, TEST_CONTENT);
    }

    @Test
    void 정상_allPostByUser() {
        //given
        List<Post> allPostsByUser = List.of(
            testPost
        );

        doReturn(Optional.of(TEST_USER)).when(userRepository).findByEmail(EMAIL);
        doReturn(allPostsByUser).when(postRepository).findAllByUser(TEST_USER);

        //when
        List<ResPostDto> resPostDtos = postService.allPostByUser(TEST_USER.getEmail());

        //then
        assertThat(resPostDtos.size()).isEqualTo(1);
        assertThat(resPostDtos.get(0).getTitle()).isEqualTo(TEST_TITLE);
        assertThat(resPostDtos.get(0).getContent()).isEqualTo(TEST_CONTENT);
        assertThat(resPostDtos.get(0).getModifiedAt()).isNull();
        assertThat(resPostDtos.get(0).getViewCnt()).isZero();
        assertThat(resPostDtos.get(0).getLikeCnt()).isZero();
    }

    @Test
    void 정상_detailPost(){
        //given
        doReturn(Optional.of(testPost)).when(postRepository).findById(1L);

        //when
        int repeatCnt = 5;
        for(int i=0;i<5;i++){
            ResPostDto resPostDto = postService.detailPost(1L);

            //then
            assertThat(resPostDto.getViewCnt()).isEqualTo(i+1);
        }
    }

    @Test
    void 정상_updatePost_title_content_모두수정() {
        //given
        String updateTitle = "test title update";
        String updateContent = "test content update";
        doReturn(Optional.of(TEST_USER)).when(userRepository).findByEmail(EMAIL);
        doReturn(Optional.of(testPost)).when(postRepository).findByIdAndUser(1L, TEST_USER);

        //when
        ResPostDto resPostDto = postService.updatePost(TEST_USER.getEmail(), 1L,
            new PostDto(updateTitle, updateContent));

        //then
        assertThat(resPostDto.getTitle()).isEqualTo(updateTitle);
        assertThat(resPostDto.getContent()).isEqualTo(updateContent);
        verify(postRepository,times(2)).findByIdAndUser(1L, TEST_USER);
    }

    @Test
    void 정상_updatePost_title_수정() {
        //given
        String updateTitle = "test title update";
        doReturn(Optional.of(TEST_USER)).when(userRepository).findByEmail(EMAIL);
        doReturn(Optional.of(testPost)).when(postRepository).findByIdAndUser(1L, TEST_USER);

        //when
        ResPostDto resPostDto = postService.updatePost(TEST_USER.getEmail(), 1L,
            new PostDto(updateTitle, null));

        //then
        assertThat(resPostDto.getTitle()).isEqualTo(updateTitle);
        assertThat(resPostDto.getContent()).isEqualTo(testPost.getContent());
        verify(postRepository,times(2)).findByIdAndUser(1L, TEST_USER);
    }

    @Test
    void 정상_updatePost_content_수정() {
        //given
        String updateContent = "test content update";
        doReturn(Optional.of(TEST_USER)).when(userRepository).findByEmail(EMAIL);
        doReturn(Optional.of(testPost)).when(postRepository).findByIdAndUser(1L, TEST_USER);

        //when
        ResPostDto resPostDto = postService.updatePost(TEST_USER.getEmail(), 1L,
            new PostDto(null, updateContent));

        //then
        assertThat(resPostDto.getTitle()).isEqualTo(testPost.getTitle());
        assertThat(resPostDto.getContent()).isEqualTo(updateContent);
        verify(postRepository,times(2)).findByIdAndUser(1L, TEST_USER);
    }

    @Test
    void 비정상_없는유저_BusinessException_호출(){
        //given
        doThrow(BusinessException.class).when(userRepository).findByEmail(any());


        //when,then
        assertThatThrownBy(() -> postService.updatePost(TEST_USER.getEmail(),1L,new PostDto()))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.allPostByUser(TEST_USER.getEmail()))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.addPost(TEST_USER.getEmail(),new PostDto()))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.deletePost(TEST_USER.getEmail(),1L))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void 비정상_없는post_BusinessException_호출(){

        //given
        doReturn(Optional.of(TEST_USER)).when(userRepository).findByEmail(any());
        doThrow(BusinessException.class).when(postRepository).findByIdAndUser(any(Long.class),any(User.class));
        doThrow(BusinessException.class).when(postRepository).findById(any(Long.class));

        //when,then
        assertThatThrownBy(() -> postService.detailPost(1L))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.deletePost(TEST_USER.getEmail(),1L))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.updatePost(TEST_USER.getEmail(),1L,new PostDto()))
            .isInstanceOf(BusinessException.class);
    }
}