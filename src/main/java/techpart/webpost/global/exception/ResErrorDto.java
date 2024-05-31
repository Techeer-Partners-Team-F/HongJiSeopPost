package techpart.webpost.global.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import techpart.webpost.global.exception.ResValidErrorDto.CustomFieldError;

@Getter
@NoArgsConstructor
public class ResErrorDto {
    private String message;
    private int status;
    private String code;

    public ResErrorDto(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
    }
}
