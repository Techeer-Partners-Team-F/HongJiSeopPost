package techpart.webpost.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpart.webpost.config.auth.LoginUser;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.repository.PostLikeRepository;
import techpart.webpost.service.PostService;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeRepository postLikeRepository;

    @GetMapping
    public ResponseEntity<List<ResPostDto>> allPost() {
        List<ResPostDto> resPostDtos = postService.allPost();
        return ResponseEntity.status(HttpStatus.OK).body(resPostDtos);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ResPostDto>> allPostByUser(@AuthenticationPrincipal LoginUser loginUser){
        String email = loginUser.getUsername();

        List<ResPostDto> resPostDtos = postService.allPostByUser(email);

        return ResponseEntity.status(HttpStatus.OK).body(resPostDtos);
    }

    @PostMapping
    public ResponseEntity<ResPostDto> addPost(@Valid @RequestBody PostDto postDto, @AuthenticationPrincipal LoginUser loginUser){
        String email = loginUser.getUsername();
        ResPostDto resPostDto = postService.addPost(email, postDto);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ResPostDto> updatePost(@PathVariable("postId") Long postId, @RequestBody PostDto postDto,@AuthenticationPrincipal LoginUser loginUser){
        String email = loginUser.getUsername();
        ResPostDto resPostDto = postService.updatePost(email, postId,postDto);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal LoginUser loginUser){
        String email = loginUser.getUsername();
        postService.deletePost(email,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResPostDto> detailPost(@PathVariable("postId") Long postId){
        ResPostDto resPostDto = postService.detailPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    /**
     * 좋아요 누르면 +1 다시 누르면 -1 및 원상 복귀
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> addLikePost(@PathVariable("postId") Long postId,@AuthenticationPrincipal LoginUser loginUser){
        String email = loginUser.getUsername();
        postService.likePost(email,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * 북마크 누르면 +1 및 추가 다시 누르면 -1 및 삭제
     */
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<?> addBookmarkPost(@PathVariable("postId") Long postId, @AuthenticationPrincipal LoginUser loginUser){
        String username = loginUser.getUsername();
        postService.bookmarkPost(username,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
