package techpart.webpost.dto.response;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.Reply;
import techpart.webpost.global.constant.Role;

@Getter
@RequiredArgsConstructor
public class ResPostDto {

    @NotEmpty
    private final ResUserDto resUserDto;

    @NotEmpty
    private final String title;

    @NotEmpty
    private final String content;

    @NotEmpty
    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    @NotEmpty
    private final int likeCnt;

    @NotEmpty
    private final int viewCnt;

    private final List<Reply> replies;

    public ResPostDto(Post post, String name, Role role, String email, List<Reply> replies) {
        this.resUserDto = new ResUserDto(name,email,role);
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.likeCnt = post.getLikeCnt();
        this.viewCnt = post.getViewCnt();
        this.replies = replies;
    }
}
