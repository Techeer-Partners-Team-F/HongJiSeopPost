package techpart.webpost.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.CrossOrigin;
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


@Tag(name = "Post", description = "Post 관련 API 입니다.")
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "모든 Post 리스트", description = "메인 화면에 들어갈 모든 포스트 리스트")
    @ApiResponse(responseCode = "200", description = "모든 Post 리스트를 정상적으로 출력하였습니다.")
    @GetMapping
    public ResponseEntity<List<ResPostDto>> allPost() {
        List<ResPostDto> resPostDtos = postService.allPost();
        return ResponseEntity.status(HttpStatus.OK).body(resPostDtos);
    }

    @Operation(summary = "해당 유저의 모든 Post 리스트", description = "해당 유저의 모든 포스트 리스트")
    @ApiResponse(responseCode = "200", description = "유저의 모든 Post 리스트를 정상적으로 출력하였습니다.")
    @GetMapping("/my")
    public ResponseEntity<List<ResPostDto>> allPostByUser(@AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        log.info("loginUser.getUser().getId() : "+loginUser.getUser().getId().toString());
        List<ResPostDto> resPostDtos = postService.allPostByUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(resPostDtos);
    }

    @Operation(summary = "Post 등록", description = "Post 등록")
    @ApiResponse(responseCode = "200", description = "Post 정상 등록하였습니다.")
    @PostMapping
    public ResponseEntity<ResPostDto> addPost(@Valid @RequestBody PostDto postDto, @AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        ResPostDto resPostDto = postService.addPost(userId, postDto);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @Operation(summary = "Post 수정", description = "Post 수정")
    @ApiResponse(responseCode = "200", description = "Post 정상 수정하였습니다.")
    @PutMapping("/{postId}")
    public ResponseEntity<ResPostDto> updatePost(@PathVariable("postId") Long postId, @RequestBody PostDto postDto,@AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        ResPostDto resPostDto = postService.updatePost(userId, postId,postDto);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @Operation(summary = "Post 삭제", description = "Post 삭제")
    @ApiResponse(responseCode = "200", description = "Post 정상 삭제하였습니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        postService.deletePost(userId,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "Post 상세 보기", description = "Post 상세 보기")
    @ApiResponse(responseCode = "200", description = "Post 정상적으로 상세보기 데이터를 응답하였습니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ResPostDto> detailPost(@PathVariable("postId") Long postId){
        ResPostDto resPostDto = postService.detailPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(resPostDto);
    }

    @Operation(summary = "Post 좋아요 추가", description = "Post 좋아요 추가, 추가된 Post에서는 좋아요 삭제")
    @ApiResponse(responseCode = "200", description = "Post 좋아요 정상 추가 또는 삭제하였습니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> addLikePost(@PathVariable("postId") Long postId,@AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        postService.likePost(userId,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "Post 북마크 추가", description = "Post 북마크 추가, 추가된 Post에서는 북마크 삭제")
    @ApiResponse(responseCode = "200", description = "Post 북마크 정상 추가 또는 삭제하였습니다.")
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<?> addBookmarkPost(@PathVariable("postId") Long postId, @AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        postService.bookmarkPost(userId,postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
