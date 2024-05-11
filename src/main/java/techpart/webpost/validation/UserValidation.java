package techpart.webpost.validation;

import org.springframework.stereotype.Component;
import techpart.webpost.global.exception.BusinessException;
import techpart.webpost.global.exception.ErrorCode;

@Component
public class UserValidation {

    public void checkEmailIsDupl(boolean isExist) {
        if(isExist){
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }
    }
}
