package techpart.webpost.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.global.constant.Role;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Builder
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "modified_at", nullable = true)
    private LocalDateTime modifiedAt;

    public User(String name, String email, String password, Role role,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.name = name;
        this.email = email;
        this.password = bCryptPasswordEncoder.encode(password);
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = null;
    }

    public void update(JoinDto joinDto){
        this.name = joinDto.getName();
        this.email = joinDto.getEmail();
        this.password = new BCryptPasswordEncoder().encode(joinDto.getPassword());
        this.role = joinDto.getRole();
        this.modifiedAt = LocalDateTime.now();
    }
}
