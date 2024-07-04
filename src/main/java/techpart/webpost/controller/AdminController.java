package techpart.webpost.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpart.webpost.domain.User;
import techpart.webpost.repository.UserRepository;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    public final UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<List<User>> allUser(){
        List<User> all = userRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }
}
