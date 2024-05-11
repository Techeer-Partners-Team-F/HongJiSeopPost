package techpart.webpost.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.JoinDto;
import techpart.webpost.dto.response.ResUserDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.service.JoinService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private JoinService joinService;

    //mock http 호출 역할
    private MockMvc mockMvc;

    @BeforeEach
    public void initMockMvc(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void 정상_회원가입() throws Exception {
        String joinEmail = "admin123@naver.com";
        String password = "admin123";
        String username ="adminHong";

        //request
        JoinDto joinDto = new JoinDto(
            username,
            joinEmail,
            password,
            Role.ADMIN
        );

        //response
        ResUserDto resUserDto = new ResUserDto(
            username,
            joinEmail,
            Role.ADMIN
        );

        doReturn(resUserDto).when(joinService).join(any(JoinDto.class));

        ResultActions performPostJoin = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(joinDto))
        );

        performPostJoin.andExpect(status().isOk())
            .andExpect(jsonPath("email",resUserDto.getEmail()).exists())
            .andExpect(jsonPath("name",resUserDto.getName()).exists())
            .andExpect(jsonPath("role",resUserDto.getRole()).exists());
    }
}