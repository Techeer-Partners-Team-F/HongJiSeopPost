package techpart.webpost.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpart.webpost.domain.Post;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.security.CustomUserDetails;
import techpart.webpost.service.PostService;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<ResPostDto>> allPost() {
        List<ResPostDto> resPostDtos = postService.allPost();
        return ResponseEntity.status(HttpStatus.OK).body(resPostDtos);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ResPostDto>> allPostByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();

        List<ResPostDto> resPostDtos = postService.allPostByUser(email);

        return ResponseEntity.status(HttpStatus.OK).body(resPostDtos);
    }

    @PostMapping
    public ResponseEntity<ResPostDto> addPost(@RequestBody PostDto postDto, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        ResPostDto resPostDto = postService.addPost(email, postDto);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ResPostDto> updatePost(@PathVariable("postId") Long postId, @RequestBody PostDto postDto,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        ResPostDto resPostDto = postService.updatePost(email, postId,postDto);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        postService.deletePost(email,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResPostDto> detailPost(@PathVariable("postId") Long postId){
        ResPostDto resPostDto = postService.detailPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }
}
