package techpart.webpost.service;

import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.Reply;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.global.exception.ErrorCode;
import techpart.webpost.repository.PostRepository;
import techpart.webpost.repository.ReplyRepository;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    public List<ResPostDto> allPost(){
        List<Post> posts =  postRepository.findAll();

        return posts.stream()
            .map((Post post) -> convertToDto(post,false))
            .collect(Collectors.toList());
    }

    public List<ResPostDto> allPostByUser(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        List<Post> allPostsByUser = postRepository.findAllByUser(user);

        return allPostsByUser.stream()
            .map((Post post) -> convertToDto(post,false))
            .collect(Collectors.toList());
    }

    public ResPostDto detailPost(Long postId){
        Post existedPost = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        existedPost.increaseViewCnt();
        return convertToDto(existedPost,true);
    }

    public ResPostDto addPost(String email, PostDto postDto){
        log.info("addPost email ={}",email);
        User postUser = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        Post newPost = new Post(postUser, postDto.getTitle(), postDto.getContent());

        postRepository.save(newPost);

        return convertToDto(newPost,true);
    }

    public ResPostDto updatePost(String email, Long postId, PostDto postDto){
        User postUser = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        Post existedPost = postRepository.findByIdAndUser(postId,postUser).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        existedPost.update(postDto);

        Post post = postRepository.findByIdAndUser(postId, postUser).get();

        return convertToDto(post,true);
    }

    public void deletePost(String email, Long postId){
        Post existedPost = getExistedPost(email, postId);

        postRepository.delete(existedPost);
//        postRepository.deleteById(postId);
    }


    public void likePost(String email, Long postId){
        Post existedPost = getExistedPost(email, postId);
        existedPost.increaseLike();
    }

    private Post getExistedPost(String email, Long postId) {
        User postUser = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return postRepository.findByIdAndUser(postId,postUser).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    private ResPostDto convertToDto(Post post, boolean getReply){
        List<Reply> allByPost = null;
        if(getReply){
            allByPost = replyRepository.findAllByPost(post);
        }
        return new ResPostDto(
            post,post.getUser().getName(),post.getUser().getRole(),post.getUser().getEmail(),allByPost
        );
    }
}
