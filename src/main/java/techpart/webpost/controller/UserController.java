package techpart.webpost.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.service.JoinService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final JoinService joinService;

    @PostMapping("/join")
    public ResponseEntity<ResUserDto> join(@RequestBody JoinDto joinDto) {
        log.info("join controller start");
        ResUserDto joined = joinService.join(joinDto);
        log.info("join complete");
        return ResponseEntity.status(HttpStatus.OK).body(joined);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(){
//        return ResponseEntity.status(HttpStatus.OK).body("login ok");
//    }
}
