package techpart.webpost.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.service.JoinService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String JOIN_EMAIL = "admin123@naver.com";
    private static final String PASSWORD = "admin123";
    private static final String USERNAME = "adminHong";

    @InjectMocks
    private UserController userController;

    @Mock
    private JoinService joinService;

    //mock http 호출 역할
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    void 정상_회원가입() throws Exception {

        //request
        JoinDto joinDto = new JoinDto(
            USERNAME,
            JOIN_EMAIL,
            PASSWORD,
            Role.ADMIN
        );

        //response
        ResUserDto resUserDto = new ResUserDto(
            USERNAME,
            JOIN_EMAIL,
            Role.ADMIN
        );

        doReturn(resUserDto).when(joinService).join(any(JoinDto.class));

        //when
        ResultActions performPostJoin = mockMvc.perform(
            getMockHttpServletRequestBuilder()
                .content(new Gson().toJson(joinDto)));

        //then
        performPostJoin.andExpect(status().isOk())
            .andExpect(jsonPath("email", resUserDto.getEmail()).value(JOIN_EMAIL))
            .andExpect(jsonPath("name", resUserDto.getName()).value(USERNAME))
            .andExpect(jsonPath("role", resUserDto.getRole()).value(Role.ADMIN.name()));
    }

    private static MockHttpServletRequestBuilder getMockHttpServletRequestBuilder() {
        return MockMvcRequestBuilders.post("/api/join")
            .contentType(MediaType.APPLICATION_JSON);
    }
}