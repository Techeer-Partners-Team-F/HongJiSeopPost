package techpart.webpost.controller;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.User;
import techpart.webpost.dto.response.ResPostDto;
import techpart.webpost.global.constant.Role;
import techpart.webpost.global.security.CustomUserDetails;
import techpart.webpost.service.PostService;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private static final String EMAIL = "admin123@naver.com";
    private static final String PASSWORD = "admin123";
    private static final String USERNAME ="adminHong";
    private static final String TEST_CONTENT = "test content";
    private static final String TEST_TITLE = "test title";

    private static final User TEST_USER = new User(USERNAME, EMAIL,PASSWORD, Role.ROLE_ADMIN,new BCryptPasswordEncoder());
    private static final CustomUserDetails CUSTOM_USER_DETAILS = new CustomUserDetails(TEST_USER);

    private Gson gson;
    private ResPostDto resPostDto;
    private UserDetails userDetails;

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private SecurityMockMvcRequestPostProcessors securityMockMvcRequestPostProcessors;

    private MockMvc mockMvc;



    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        Post testPost = new Post(TEST_USER, TEST_TITLE, TEST_CONTENT);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();
        resPostDto = new ResPostDto(
            testPost, USERNAME, Role.ROLE_ADMIN, EMAIL, null
        );
    }

    @Test
    void allPost() throws Exception {
        //given
        doReturn(List.of(resPostDto)).when(postService).allPost();

        //when
        log.info(gson.toJson(List.of(resPostDto)));
        ResultActions performGet = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/post")
                    .contentType(MediaType.APPLICATION_JSON));

        performGet.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title",TEST_TITLE).exists())
            .andExpect(jsonPath("$[0].content",TEST_CONTENT).exists())
            .andExpect(jsonPath("$[0].viewCnt",0).exists())
            .andExpect(jsonPath("$[0].resUserDto").exists())
            .andExpect(jsonPath("$[0].resUserDto.name",USERNAME).exists());
    }

//    @Test
//    void allPostByUser_UserDetails확인() throws Exception {
//        //given
//        doReturn(List.of(resPostDto)).when(postService).allPostByUser(EMAIL);
//
//        //when
//        log.info(gson.toJson(List.of(resPostDto)));
//        ResultActions performGet = mockMvc.perform(
//            MockMvcRequestBuilders.get("/api/post/my")
//                .with(SecurityMockMvcRequestPostProcessors.user(CUSTOM_USER_DETAILS))
//                .contentType(MediaType.APPLICATION_JSON));
//
//        performGet.andExpect(status().isOk())
//            .andExpect(jsonPath("$[0].title",TEST_TITLE).exists())
//            .andExpect(jsonPath("$[0].content",TEST_CONTENT).exists())
//            .andExpect(jsonPath("$[0].viewCnt",0).exists())
//            .andExpect(jsonPath("$[0].resUserDto").exists())
//            .andExpect(jsonPath("$[0].resUserDto.name",USERNAME).exists());
//    }

    //Gson이 LocalDateTime를 읽을 수 있도록 추가 클래스 작성후 gsonBuilder에 적용
    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(localDateTime));
        }
    }

    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.KOREA));
        }
    }
}