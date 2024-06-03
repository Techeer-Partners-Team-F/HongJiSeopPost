package techpart.webpost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    @NotEmpty
    @NotBlank
    private String title;

    @NotEmpty
    @NotBlank
    private String content;
}
