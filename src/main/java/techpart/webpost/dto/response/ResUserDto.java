package techpart.webpost.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import techpart.webpost.global.constant.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
    @NotEmpty
    private Role role;
}
