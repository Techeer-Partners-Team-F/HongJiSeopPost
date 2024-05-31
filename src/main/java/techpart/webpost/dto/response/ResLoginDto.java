package techpart.webpost.dto.response;

import lombok.Getter;
import techpart.webpost.domain.User;
import techpart.webpost.util.CustomDateUtil;

@Getter
public class ResLoginDto {
    private final Long id;
    private final String email;
    private final String createdAt;

    public ResLoginDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
    }
}
