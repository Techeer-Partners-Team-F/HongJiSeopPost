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
import techpart.webpost.domain.PostLike;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.repository.PostLikeRepository;
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
    private static Post testPost;

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

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

//        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
//        when(postRepository.findAllByUser(TEST_USER)).thenReturn(allPostsByUser);
//
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(TEST_USER));
        given(postRepository.findAllByUser(TEST_USER)).willReturn(allPostsByUser);

        //when
        List<ResPostDto> resPostDtos = postService.allPostByUser(TEST_USER.getEmail());

        //then
        verify(postRepository).findAllByUser(TEST_USER);
        verify(userRepository).findByEmail(EMAIL);
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
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(testPost));

        //when
        int repeatCnt = 5;
        for(int i=0;i<repeatCnt;i++){
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
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        when(postRepository.findByIdAndUser(1L,TEST_USER)).thenReturn(Optional.of(testPost));
        //when
        ResPostDto resPostDto = postService.updatePost(TEST_USER.getEmail(), 1L,
            new PostDto(updateTitle, updateContent));

        //then
        assertThat(resPostDto.getTitle()).isEqualTo(updateTitle);
        assertThat(resPostDto.getContent()).isEqualTo(updateContent);
        verify(postRepository).findByIdAndUser(1L, TEST_USER);
    }

    @Test
    void 정상_updatePost_title_수정() {
        //given
        String updateTitle = "test title update";
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        when(postRepository.findByIdAndUser(1L,TEST_USER)).thenReturn(Optional.of(testPost));

        //when
        ResPostDto resPostDto = postService.updatePost(TEST_USER.getEmail(), 1L,
            new PostDto(updateTitle, null));

        //then
        assertThat(resPostDto.getTitle()).isEqualTo(updateTitle);
        assertThat(resPostDto.getContent()).isEqualTo(testPost.getContent());
        verify(postRepository,times(1)).findByIdAndUser(1L, TEST_USER);
    }

    @Test
    void 정상_updatePost_content_수정() {
        //given
        String updateContent = "test content update";
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        when(postRepository.findByIdAndUser(1L,TEST_USER)).thenReturn(Optional.of(testPost));
        //when
        ResPostDto resPostDto = postService.updatePost(TEST_USER.getEmail(), 1L,
            new PostDto(null, updateContent));

        //then
        assertThat(resPostDto.getTitle()).isEqualTo(testPost.getTitle());
        assertThat(resPostDto.getContent()).isEqualTo(updateContent);
        verify(postRepository,times(1)).findByIdAndUser(1L, TEST_USER);
    }

    @Test
    void 정상_likePost_좋아요추가(){
        PostLike postLike = getPostLike();
        //given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.findByUserAndPost(TEST_USER,testPost)).thenReturn(Optional.ofNullable(null));
        when(postLikeRepository.save(any(PostLike.class))).thenReturn(postLike);

        //when
        assertThat(testPost.getLikeCnt()).isEqualTo(0);
        postService.likePost(EMAIL,1L);

        //then
        assertThat(testPost.getLikeCnt()).isEqualTo(1);
        verify(postLikeRepository).save(any(PostLike.class));
        verify(postLikeRepository,never()).delete(any());
    }

    private static PostLike getPostLike() {
        return PostLike.builder()
            .user(TEST_USER)
            .post(testPost)
            .build();
    }

    @Test
    void 정상_likePost_좋아요가_있는경우_삭제(){
        PostLike postLike = getPostLike();
        //given
        testPost.increaseLike();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.findByUserAndPost(TEST_USER,testPost)).thenReturn(
            Optional.of(postLike));
        doNothing().when(postLikeRepository).delete(postLike);

        //when
        assertThat(testPost.getLikeCnt()).isEqualTo(1);
        postService.likePost(EMAIL,1L);

        //then
        assertThat(testPost.getLikeCnt()).isEqualTo(0);
        verify(postLikeRepository).delete(postLike);
        verify(postLikeRepository,never()).save(postLike);
    }

    @Test
    void 비정상_없는유저_BusinessException_호출(){
        //given
//        when(userRepository.findByEmail(any())).thenThrow(BusinessException.class);
        given(userRepository.findByEmail(any())).willThrow(BusinessException.class);

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
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(TEST_USER));
        when(postRepository.findByIdAndUser(any(Long.class),any(User.class))).thenThrow(BusinessException.class);
        when(postRepository.findById(any(Long.class))).thenThrow(BusinessException.class);

        //when,then
        assertThatThrownBy(() -> postService.detailPost(1L))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.deletePost(TEST_USER.getEmail(),1L))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.updatePost(TEST_USER.getEmail(),1L,new PostDto()))
            .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> postService.likePost(TEST_USER.getEmail(),1L))
            .isInstanceOf(BusinessException.class);
    }
}