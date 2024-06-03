package techpart.webpost.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpart.webpost.domain.User;
import techpart.webpost.repository.UserRepository;

@Tag(name = "Admin", description = "관리자 기능 관련 API 입니다.")
@CrossOrigin
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    public final UserRepository userRepository;

    @Operation(summary = "모든 유저 리스트", description = "관리자가 볼 수 있는 유저 리스트")
    @ApiResponse(responseCode = "200", description = "모든 유저 리스트를 정상 응답하였습니다.")
    @GetMapping("/user")
    public ResponseEntity<List<User>> allUser(){
        List<User> all = userRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }
}
