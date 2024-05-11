package techpart.webpost.dto.response;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import techpart.webpost.domain.Post;

@Getter
@RequiredArgsConstructor
public class ResReplyDto {

    @NotEmpty
    private final ResUserDto resUserDto;

    @NotEmpty
    private final Post post;

    @NotEmpty
    private final String content;

    @NotEmpty
    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    @NotEmpty
    private final int likeCnt;
}
