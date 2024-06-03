package techpart.webpost.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import techpart.webpost.global.constant.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto{

    @NotEmpty
    @NotBlank
    private String name;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @NotBlank
    private String password;

    //jsonCraetor 적용
    @NotNull(message = "잘못된 입력값입니다.")
    private Role role;
}
