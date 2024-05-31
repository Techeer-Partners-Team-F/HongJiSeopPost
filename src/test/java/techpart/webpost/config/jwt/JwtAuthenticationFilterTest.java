package techpart.webpost.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static techpart.webpost.dummy.DummyObject.RAW_PASSWORD;
import static techpart.webpost.dummy.DummyObject.newUserForTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import techpart.webpost.dto.request.LoginDto;
import techpart.webpost.repository.UserRepository;

@Slf4j
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class JwtAuthenticationFilterTest {
    private static final String TEST_EMAIL = "adh123@naver.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.save(newUserForTest(TEST_EMAIL));
    }


    @Test
    void attemptAuthentication() {
    }

    @Test
    void successfulAuthentication() throws Exception {
        //given
        LoginDto loginDto = new LoginDto(TEST_EMAIL, RAW_PASSWORD);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
            .content(new Gson().toJson(loginDto))
            .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);

        log.info("responseBody : " + responseBody);
        log.info("jwtToken : " + jwtToken);

        resultActions.andExpect(status().isOk());
        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken).startsWith(JwtVO.TOKEN_PREFIX);
        resultActions.andExpect(jsonPath("$.data.email").value(TEST_EMAIL));
        //Body = {"code":1,"msg":"로그인성공","data":{"id":3,"email":"adh123@naver.com","createdAt":"2024-05-30 23:51:29"}}

    }

    @Test
    void unsuccessfulAuthentication() throws Exception {

        //given
        LoginDto loginDto = new LoginDto(TEST_EMAIL, RAW_PASSWORD+"1");

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
            .content(new Gson().toJson(loginDto))
            .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);

        log.info("responseBody : " + responseBody);
        log.info("jwtToken : " + jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized()); //401
    }
}