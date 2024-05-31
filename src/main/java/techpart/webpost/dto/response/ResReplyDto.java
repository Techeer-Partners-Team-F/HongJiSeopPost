package techpart.webpost.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
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
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedAt;

    @NotEmpty
    private final int likeCnt;
}
