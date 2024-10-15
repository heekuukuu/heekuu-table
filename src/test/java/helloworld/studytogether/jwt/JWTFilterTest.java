package helloworld.studytogether;

import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class JWTFilterTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JWTUtil jwtUtil;

  @MockBean
  private UserRepository userRepository;

  private String accessToken;

  @BeforeEach
  public void setup() {
    User user = new User();
    user.setUserId(1L);
    user.setUsername("testUser");

    accessToken = "mockAccessToken";  // JWT 토큰 생성 시 필요한 값을 설정

    // Mock 설정: 토큰 검증 및 사용자 정보 반환
    Mockito.when(jwtUtil.getUserId(accessToken)).thenReturn(1L);
    Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
  }

  @Test
  public void testAuthenticatedRequest() throws Exception {
    mockMvc.perform(get("/api/protected-endpoint")  // 보호된 엔드포인트 요청
            .header("Authorization", "Bearer " + accessToken))  // Access Token 추가
        .andExpect(status().isOk());  // 성공적인 요청 검증
  }

  @Test
  public void testUnauthorizedRequest() throws Exception {
    mockMvc.perform(get("/api/protected-endpoint"))  // 토큰 없이 요청
        .andExpect(status().isUnauthorized());  // 401 반환 검증
  }
}