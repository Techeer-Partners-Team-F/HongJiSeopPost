package techpart.webpost.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import techpart.webpost.dto.response.ResSecuExDto;

@Slf4j
public class CustomResponseUtil {

    public static void success(HttpServletResponse response, Object dto) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResSecuExDto<?> resSecuExDto = new ResSecuExDto<>(1, "로그인성공", dto);
            String responseBody = om.writeValueAsString(resSecuExDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(responseBody);
        } catch (Exception e) { //사실 파싱 에러가 날 수 없는 구조다.
            log.error("서버 파싱 에러");
        }
    }

    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResSecuExDto<?> resSecuExDto = new ResSecuExDto<>(-1, msg, null);
            String responseBody = om.writeValueAsString(resSecuExDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody);
        } catch (Exception e) { //사실 파싱 에러가 날 수 없는 구조다.
            log.error("서버 파싱 에러");
        }
    }


}
