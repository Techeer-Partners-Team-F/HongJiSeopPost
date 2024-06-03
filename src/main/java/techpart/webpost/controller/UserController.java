package techpart.webpost.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpart.webpost.config.auth.LoginUser;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.dto.response.ResUserInfoDto;
import techpart.webpost.service.UserService;


@Tag(name = "User", description = "User 관련 API 입니다.")
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입", description = "이메일과 패스워드를 통한 회원가입")
    @ApiResponse(responseCode = "200", description = "회원가입 정상적으로 완료하였습니다.")
    @PostMapping("/join")
    public ResponseEntity<ResUserDto> join(@Valid @RequestBody JoinDto joinDto) {
        log.info("join controller start");
        ResUserDto joined = userService.join(joinDto);
        log.info("join complete");
        return ResponseEntity.status(HttpStatus.OK).body(joined);
    }
    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정")
    @ApiResponse(responseCode = "200", description = "회원 정보 정상 수정되었습니다.")
    @PostMapping("/my")
    public ResponseEntity<?> updateMyUserInfo(@RequestBody JoinDto joinDto, @AuthenticationPrincipal
        LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        userService.updateUserInfo(joinDto,userId);
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "회원 정보", description = "회원 정보")
    @ApiResponse(responseCode = "200", description = "회원 정보 정상 응답하였습니다.")
    @GetMapping("/my")
    public ResponseEntity<ResUserInfoDto> getMyUserInfo(@RequestBody JoinDto joinDto, @AuthenticationPrincipal LoginUser loginUser){
        Long userId = loginUser.getUser().getId();
        ResUserInfoDto userInfo = userService.getUserInfo(joinDto, userId);
        return ResponseEntity.ok().body(userInfo);
    }
}
