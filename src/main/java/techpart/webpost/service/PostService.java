package techpart.webpost.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import techpart.webpost.domain.Bookmark;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.PostLike;
import techpart.webpost.domain.Reply;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.global.exception.ErrorCode;
import techpart.webpost.repository.BookmarkRepository;
import techpart.webpost.repository.PostLikeRepository;
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
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;

    public List<ResPostDto> allPost(){
        List<Post> posts =  postRepository.findAll();

        return posts.stream()
            .map((Post post) -> convertToDto(post,false))
            .collect(Collectors.toList());
    }

    public List<ResPostDto> allPostByUser(Long userId){
        User user = getUserOrThrowException(userId);
        List<Post> allPostsByUser = postRepository.findAllByUser(user);

        return allPostsByUser.stream()
            .map((Post post) -> convertToDto(post,false))
            .collect(Collectors.toList());
    }

    public ResPostDto detailPost(Long postId){
        Post existedPost = getPostOrThrowException(postId);
        existedPost.increaseViewCnt();
        return convertToDto(existedPost,true);
    }

    public ResPostDto addPost(Long userId, PostDto postDto){
        User postUser = getUserOrThrowException(userId);
        Post newPost = new Post(postUser, postDto.getTitle(), postDto.getContent());

        postRepository.save(newPost);

        return convertToDto(newPost,true);
    }

    public ResPostDto updatePost(Long userId, Long postId, PostDto postDto){
        User postUser = getUserOrThrowException(userId);
        Post existedPost = getPostOrThrowException(postId, postUser);

        existedPost.update(postDto);

        return convertToDto(existedPost,true);
    }

    public void deletePost(Long userId, Long postId){
        User postUser = getUserOrThrowException(userId);
        Post existedPost = getPostOrThrowException(postId, postUser);

        postRepository.delete(existedPost);
    }

    public void likePost(Long userId, Long postId){
        Post existedPost = getPostOrThrowException(postId);
        User user = getUserOrThrowException(userId);
        Optional<PostLike> postLikeByUserAndPost = postLikeRepository.findByUserAndPost(user, existedPost);
        if(postLikeByUserAndPost.isPresent()){
            existedPost.decreaseLikeCnt();
            postLikeRepository.delete(postLikeByUserAndPost.get());
            return;
        }
        existedPost.increaseLikeCnt();
        PostLike newPostLike = PostLike.builder()
            .post(existedPost)
            .user(user)
            .build();
        postLikeRepository.save(newPostLike);
    }

    public void bookmarkPost(Long userId,Long postId){
        Post existedPost = getPostOrThrowException(postId);
        User user = getUserOrThrowException(userId);
        Optional<Bookmark> byUserAndPost = bookmarkRepository.findByUserAndPost(user, existedPost);
        if (byUserAndPost.isPresent()){
            existedPost.decreaseBookmarkCnt();
            bookmarkRepository.delete(byUserAndPost.get());
            return;
        }
        existedPost.increaseBookmarkCnt();
        Bookmark newBookmark = Bookmark.builder()
            .post(existedPost)
            .user(user)
            .build();
        bookmarkRepository.save(newBookmark);
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Post getPostOrThrowException(Long postId, User postUser) {
        return postRepository.findByIdAndUser(postId, postUser).orElseThrow(() -> new BusinessException(ErrorCode.POST_USER_MISMATCH));
    }

    private Post getPostOrThrowException(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private ResPostDto convertToDto(Post post, boolean getReply){
        List<Reply> allByPost = null;
        if(getReply){
            allByPost = replyRepository.findAllByPost(post);
        }
        return new ResPostDto(
            post,
            allByPost
        );
    }
}