package techpart.webpost.config.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import techpart.webpost.config.auth.LoginUser;
import techpart.webpost.dummy.DummyObject;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class JwtAuthorizationFilterTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtProcess jwtProcess;

    @Test
    void jwt_authorization_success_test_404() throws Exception {
        // given
        LoginUser loginUser = new LoginUser(DummyObject.newUserForTest("adc@naver.com"));
        String jwtToken = jwtProcess.create(loginUser);
        log.info("jwtToken : " + jwtToken);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/post/not/found/uri/test")
            .header(JwtVO.HEADER, jwtToken));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody : "+responseBody);

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void authorization_fail_test_헤더에_토큰없음_401() throws Exception {
        // given
        // 토큰을 해더에 넣지 않음

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/post"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info(responseBody);
        // then
        resultActions.andExpect(status().isUnauthorized()); // 401
    }

    @Test
    public void authorization_admin_test_CUSTOMER유저로_ADMIN주소접속시도_403() throws Exception{
// given
        LoginUser loginUser = new LoginUser(DummyObject.newUserForTest("adc@naver.com"));
        String jwtToken = jwtProcess.create(loginUser);
        log.info("jwtToken : " + jwtToken);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/admin")
            .header(JwtVO.HEADER, jwtToken));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info(responseBody);

        // then
        resultActions.andExpect(status().isForbidden()); // 403
    }
}