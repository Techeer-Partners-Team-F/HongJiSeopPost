package techpart.webpost.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResSecuExDto<T> {
    private final Integer code; // 1 성공, -1 실패
    private final String msg;
    private final T data; //데이터의 종류가 많으므로 제네릭으로 설정
}
