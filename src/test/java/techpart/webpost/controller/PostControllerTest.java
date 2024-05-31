package techpart.webpost.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static techpart.webpost.dummy.DummyObject.TEST_CONTENT;
import static techpart.webpost.dummy.DummyObject.TEST_EMAIL;
import static techpart.webpost.dummy.DummyObject.TEST_TITLE;
import static techpart.webpost.dummy.DummyObject.TEST_USERNAME;
import static techpart.webpost.dummy.DummyObject.newUserForTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import techpart.webpost.config.jwt.JwtVO;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.User;
import techpart.webpost.dto.request.PostDto;
import techpart.webpost.repository.BookmarkRepository;
import techpart.webpost.repository.PostLikeRepository;
import techpart.webpost.repository.PostRepository;
import techpart.webpost.repository.UserRepository;
import techpart.webpost.service.PostService;

@Slf4j
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class PostControllerTest {

    private static final User TEST_USER = newUserForTest(TEST_EMAIL);
    private static Gson gson;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;


    @BeforeAll
    static void init(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @BeforeEach
    void initData(){
        userRepository.save(TEST_USER);
        User user = userRepository.findByEmail(TEST_EMAIL).get();
        postRepository.save(new Post(user,TEST_TITLE,TEST_CONTENT));
        em.clear();

        List<Post> all = postRepository.findAll();
        log.info("post list size : "+all.size());
        for (Post post : all) {
            log.info("post id : "+post.getId());
        }
    }

    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 정상_allPost_GET() throws Exception {
        //given

        //when
        ResultActions performGet = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/post")
                    .contentType(MediaType.APPLICATION_JSON));

        //then
        performGet.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title",TEST_TITLE).exists())
            .andExpect(jsonPath("$[0].content",TEST_CONTENT).exists())
            .andExpect(jsonPath("$[0].viewCnt",0).exists())
            .andExpect(jsonPath("$[0].resUserDto").exists())
            .andExpect(jsonPath("$[0].resUserDto.name", TEST_USERNAME).exists());
    }


    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 정상_allPostByUser_GET() throws Exception {
        //given

        //when
        ResultActions performGet = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = performGet.andReturn().getResponse().getContentAsString();
        String jwtToken = performGet.andReturn().getResponse().getHeader(JwtVO.HEADER);

        log.info("responseBody : " + responseBody);
        log.info("jwtToken : " + jwtToken);

        //then
        performGet.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title",TEST_TITLE).exists())
            .andExpect(jsonPath("$[0].content",TEST_CONTENT).exists())
            .andExpect(jsonPath("$[0].viewCnt",0).exists())
            .andExpect(jsonPath("$[0].resUserDto").exists())
            .andExpect(jsonPath("$[0].resUserDto.name", TEST_USERNAME).exists());
    }

    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 비정상_addPost_POST_Vaild_annotation_예외호출() throws Exception {
        //given
        PostDto postDto = new PostDto("","content");

        //when
        ResultActions performGet = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(postDto)));

        //then
        String responseBody = performGet.andReturn().getResponse().getContentAsString();
        String jwtToken = performGet.andReturn().getResponse().getHeader(JwtVO.HEADER);

        log.info("responseBody : " + responseBody);
        log.info("jwtToken : " + jwtToken);

        performGet.andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 정상_addLikePost_like추가() throws Exception {
        //when
        Post post = postRepository.findAll().get(0);
        ResultActions performGet = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/post/"+post.getId()+"/like"));

        //then
        performGet.andExpect(status().isOk());

        //postlike 확인
        User user = userRepository.findByEmail(TEST_EMAIL).get();
        post = postRepository.findAll().get(0);

        assertThat(postLikeRepository.findByUserAndPost(user,post).isPresent()).isTrue();
        assertThat(post.getLikeCnt()).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 정상_addLikePost_like추가후_삭제() throws Exception {
        //given
        Post post1 = postRepository.findAll().get(0);
        postService.likePost(TEST_EMAIL,post1.getId());

        //when
        ResultActions performPost1 = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/post/"+post1.getId()+"/like"));

        //then
        performPost1.andExpect(status().isOk());

        //postlike 확인
        User user = userRepository.findByEmail(TEST_EMAIL).get();
        Post post = postRepository.findAll().get(0);

        assertThat(postLikeRepository.findByUserAndPost(user,post).isPresent()).isFalse();
        assertThat(post.getLikeCnt()).isEqualTo(0);
    }

    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 정상_addBookmarkPost_Bookmark추가() throws Exception{
        //when
        Post post = postRepository.findAll().get(0);
        ResultActions performPost = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/post/"+post.getId()+"/bookmark"));

        //then
        performPost.andExpect(status().isOk());

        //bookmark 확인
        User user = userRepository.findByEmail(TEST_EMAIL).get();
        post = postRepository.findAll().get(0);

        assertThat(bookmarkRepository.findByUserAndPost(user,post).isPresent()).isTrue();
        assertThat(post.getBookmarkCnt()).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = TEST_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 정상_addBookmarkPost_bookmark추가후_삭제() throws Exception {
        //given
        Post post = postRepository.findAll().get(0);
        postService.bookmarkPost(TEST_EMAIL,post.getId());

        //when
        ResultActions performPost1 = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/post/"+post.getId()+"/bookmark"));
        //then
        performPost1.andExpect(status().isOk());

        //postlike 확인
        User user = userRepository.findByEmail(TEST_EMAIL).get();
        post = postRepository.findAll().get(0);

        assertThat(bookmarkRepository.findByUserAndPost(user,post).isPresent()).isFalse();
        assertThat(post.getLikeCnt()).isEqualTo(0);
    }



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