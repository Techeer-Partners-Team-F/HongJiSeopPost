package techpart.webpost.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import techpart.webpost.global.constant.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto{

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    @Enumerated(value = EnumType.STRING)
    private Role role;
}
