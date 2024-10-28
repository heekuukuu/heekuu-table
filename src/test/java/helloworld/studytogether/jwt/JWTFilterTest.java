//package helloworld.studytogether.jwt;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import helloworld.studytogether.jwt.util.JWTUtil;
//import helloworld.studytogether.user.entity.Role;
//import helloworld.studytogether.user.entity.User;
//import helloworld.studytogether.user.repository.UserRepository;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import java.security.Key;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.test.web.servlet.MockMvc;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class JWTFilterTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @MockBean
//  private JWTUtil jwtUtil;
//
//  @MockBean
//  private UserRepository userRepository;
//
//  private User testUser;
//  private String validToken;
//  private String secret = "testSecretKeyFosdadwadaojjjadalwdladalndananksdnrJWTUtilTesting1234567890"; // Example secret key for testing
//
//  private Key getSigningKey() {
//    byte[] keyBytes = Decoders.BASE64.decode(secret);
//    return Keys.hmacShaKeyFor(keyBytes);
//  }
//
//  @BeforeEach
//  void setUp() {
//    testUser = new User();
//    testUser.setUserId(1L);
//    testUser.setEmail("test@example.com");
//    testUser.setNickname("testuser");
//    testUser.setRole(Role.USER);
//
//    // JWT 유효한 토큰 예시 생성
//    validToken = "Bearer " + Jwts.builder()
//        .claim("userid", 1L)
//        .claim("tokenType", "access")
//        .claim("role", "USER")
//        .signWith(getSigningKey())
//        .compact();
//  }
//
//  @Test
//  @DisplayName("필터를 로그인, 회원가입, 리프레시 토큰 경로에서 건너뛰는지 확인")
//  void testFilterBypassOnCertainPaths() throws Exception {
//    mockMvc.perform(get("/login"))
//        .andExpect(status().isOk());
//
//    mockMvc.perform(get("/join"))
//        .andExpect(status().isOk());
//
//    mockMvc.perform(get("/reissue"))
//        .andExpect(status().isOk());
//  }
//
//  @Test
//  @DisplayName("유효한 JWT 토큰이 주어졌을 때 필터가 인증을 수행하는지 확인")
//  void testValidJwtTokenAuthentication() throws Exception {
//    Mockito.when(jwtUtil.isExpired(Mockito.anyString())).thenReturn(false);
//    Mockito.when(jwtUtil.getUserId(Mockito.anyString())).thenReturn(1L);
//    Mockito.when(userRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.of(testUser));
//    Mockito.when(jwtUtil.getTokenType(Mockito.anyString())).thenReturn("access");
//    Mockito.when(jwtUtil.getRole(Mockito.anyString())).thenReturn("USER");
//
//    mockMvc.perform(get("/some-secured-path")
//            .header(HttpHeaders.AUTHORIZATION, validToken))
//        .andExpect(status().isOk());
//  }
//
//  @Test
//  @DisplayName("만료된 JWT 토큰이 주어졌을 때 401 상태를 반환하는지 확인")
//  void testExpiredJwtToken() throws Exception {
//    Mockito.when(jwtUtil.isExpired(Mockito.anyString())).thenThrow(new ExpiredJwtException(null, null, "Expired token"));
//
//    mockMvc.perform(get("/some-secured-path")
//            .header(HttpHeaders.AUTHORIZATION, validToken))
//        .andExpect(status().isUnauthorized());
//  }
//
//  @Test
//  @DisplayName("유효하지 않은 JWT 토큰이 주어졌을 때 401 상태를 반환하는지 확인")
//  void testInvalidJwtToken() throws Exception {
//    Mockito.when(jwtUtil.isExpired(Mockito.anyString())).thenReturn(false);
//    Mockito.when(jwtUtil.getTokenType(Mockito.anyString())).thenReturn("invalid");
//
//    mockMvc.perform(get("/some-secured-path")
//            .header(HttpHeaders.AUTHORIZATION, validToken))
//        .andExpect(status().isUnauthorized());
//  }
//
//  @Test
//  @DisplayName("권한이 부족한 경우 403 상태를 반환하는지 확인")
//  void testInsufficientRole() throws Exception {
//    Mockito.when(jwtUtil.isExpired(Mockito.anyString())).thenReturn(false);
//    Mockito.when(jwtUtil.getUserId(Mockito.anyString())).thenReturn(1L);
//    Mockito.when(userRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.of(testUser));
//    Mockito.when(jwtUtil.getTokenType(Mockito.anyString())).thenReturn("access");
//    Mockito.when(jwtUtil.getRole(Mockito.anyString())).thenReturn("GUEST");
//
//    mockMvc.perform(get("/some-secured-path")
//            .header(HttpHeaders.AUTHORIZATION, validToken))
//        .andExpect(status().isForbidden());
//  }
//}