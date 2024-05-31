package techpart.webpost.global.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Getter
@NoArgsConstructor
public class ResValidErrorDto {

    private String message;
    private int status;
    private List<CustomFieldError> errors;
    private String code;


    private ResValidErrorDto(final ErrorCode code, final List<CustomFieldError> errors) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.errors = errors;
        this.code = code.getCode();
    }

    public ResValidErrorDto(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>();
    }


    public static ResValidErrorDto of(final ErrorCode code, final BindingResult bindingResult) {
        return new ResValidErrorDto(code, CustomFieldError.of(bindingResult));
    }

    public static ResValidErrorDto of(final ErrorCode code) {
        return new ResValidErrorDto(code);
    }

    public static ResValidErrorDto of(final ErrorCode code, final List<CustomFieldError> errors) {
        return new ResValidErrorDto(code, errors);
    }

    public static ResValidErrorDto of(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final List<CustomFieldError> errors = CustomFieldError.of(e.getName(), value, e.getErrorCode());
        return new ResValidErrorDto(ErrorCode.INVALID_TYPE_VALUE, errors);
    }


    @Getter
    @NoArgsConstructor
    public static class CustomFieldError {
        private String field;
        private String rejectedValue;
        private String reason;

        private CustomFieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.rejectedValue = value;
            this.reason = reason;
        }

        public static List<CustomFieldError> of(final String field, final String value, final String reason) {
            List<CustomFieldError> customFieldErrors = new ArrayList<>();
            customFieldErrors.add(new CustomFieldError(field, value, reason));
            return customFieldErrors;
        }

        private static List<CustomFieldError> of(final BindingResult bindingResult) {
            final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error -> new CustomFieldError(
                    error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage()))
                .collect(Collectors.toList());
        }
    }


}