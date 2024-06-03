package techpart.webpost.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
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
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedAt;

    @Min(value = 0)
    @NotEmpty
    private final int likeCnt;

    @Min(value = 0)
    @NotEmpty
    private final int viewCnt;

    private final List<Reply> replies;

    public ResPostDto(Post post, List<Reply> replies) {
        this.resUserDto = new ResUserDto(post.getUser().getName(),
            post.getUser().getEmail(),
            post.getUser().getRole()
        );
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.likeCnt = post.getLikeCnt();
        this.viewCnt = post.getViewCnt();
        this.replies = replies;
    }
}
