package techpart.webpost.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.Reply;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.repository.PostRepository;
import techpart.webpost.repository.ReplyRepository;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    public List<Post> allPost(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException());
        return postRepository.findAllByUser(user);
    }

    public ResPostDto detailPost(Long userId, Long postId){
        User postUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException());
        Post existedPost = postRepository.findByIdAndUser(postId,postUser).orElseThrow(() -> new IllegalArgumentException());

        List<Reply> allByPost = replyRepository.findAllByPost(existedPost);
        return new ResPostDto(existedPost, postUser.getName(),postUser.getRole(),postUser.getEmail(),allByPost);
    }

    public ResPostDto addPost(Long userId, PostDto postDto){
        User postUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException());
        Post newPost = Post.builder()
            .user(postUser)
            .title(postDto.getTitle())
            .content(postDto.getContent())
            .createdAt(LocalDateTime.now())
            .viewCnt(0)
            .likeCnt(0)
            .build();

        List<Reply> allByPost = replyRepository.findAllByPost(newPost);
        return new ResPostDto(newPost,postUser.getName(),postUser.getRole(), postUser.getEmail(),
            allByPost);
    }

    public ResPostDto updatePost(Long userId, Long postId, PostDto postDto){
        User postUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException());
        Post existedPost = postRepository.findByIdAndUser(postId,postUser).orElseThrow(() -> new IllegalArgumentException());

        existedPost.update(postDto);

        List<Reply> allByPost = replyRepository.findAllByPost(existedPost);
        return new ResPostDto(existedPost, postUser.getName(),postUser.getRole(),
            postUser.getEmail(), allByPost);
    }

    public void deletePost(Long userId, Long postId){
        User postUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException());
        Post existedPost = postRepository.findByIdAndUser(postId,postUser).orElseThrow(() -> new IllegalArgumentException());

        postRepository.delete(existedPost);
//        postRepository.deleteById(postId);
    }


}
