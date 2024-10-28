package helloworld.studytogether;

import helloworld.studytogether.user.dto.JoinDTO;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import helloworld.studytogether.user.service.JoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JoinServiceTest {

  // 리포지토리와 암호화 객체를 Mocking합니다. 실제 데이터베이스와 암호화 동작을 대체하여 테스트 환경을 설정합니다.
  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  // JoinService를 테스트하기 위해 Mock 객체들을 주입합니다.
  @InjectMocks
  private JoinService joinService;

  // 테스트 실행 전에 Mock 객체들을 초기화합니다.
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 테스트 1: 회원 가입이 정상적으로 처리되는지 확인하는 테스트입니다.
  @Test
  void joinProcess_Success() {
    // given: 테스트에 필요한 데이터 준비
    JoinDTO joinDTO = new JoinDTO("testuser", "password", "test@test.com", "nickname");

    // Mock 설정: 해당 username, email, nickname이 존재하지 않는다고 설정
    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(userRepository.existsByNickname("nickname")).thenReturn(false);
    when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");

    // when: joinProcess 메서드를 실행
    joinService.joinProcess(joinDTO);

    // then: 저장 메서드가 1번 호출되었는지 확인
    verify(userRepository, times(1)).save(any(User.class));
  }

  // 테스트 2: 이미 등록된 아이디가 있을 경우 예외가 발생하는지 확인하는 테스트입니다.
  @Test
  void joinProcess_ThrowsException_WhenUsernameExists() {
    // given: 이미 존재하는 username
    JoinDTO joinDTO = new JoinDTO("testuser", "password", "test@test.com", "nickname");

    // Mock 설정: 해당 username이 존재한다고 설정
    when(userRepository.existsByUsername("testuser")).thenReturn(true);

    // then: RuntimeException이 발생하는지 확인
    assertThrows(RuntimeException.class, () -> joinService.joinProcess(joinDTO));
  }

  // 테스트 3: 이미 등록된 이메일이 있을 경우 예외가 발생하는지 확인하는 테스트입니다.
  @Test
  void joinProcess_ThrowsException_WhenEmailExists() {
    // given: 이미 존재하는 email
    JoinDTO joinDTO = new JoinDTO("testuser", "password", "test@test.com", "nickname");

    // Mock 설정: 해당 username은 존재하지 않지만 email이 존재한다고 설정
    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

    // then: RuntimeException이 발생하는지 확인
    assertThrows(RuntimeException.class, () -> joinService.joinProcess(joinDTO));
  }

  // 테스트 4: 이미 등록된 닉네임이 있을 경우 예외가 발생하는지 확인하는 테스트입니다.
  @Test
  void joinProcess_ThrowsException_WhenNicknameExists() {
    // given: 이미 존재하는 nickname
    JoinDTO joinDTO = new JoinDTO("testuser", "password", "test@test.com", "nickname");

    // Mock 설정: username, email은 존재하지 않지만 nickname이 존재한다고 설정
    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(userRepository.existsByNickname("nickname")).thenReturn(true);

    // then: RuntimeException이 발생하는지 확인
    assertThrows(RuntimeException.class, () -> joinService.joinProcess(joinDTO));
  }

  // 테스트 5: 비밀번호가 입력되지 않은 경우 예외가 발생하는지 확인하는 테스트입니다.
  @Test
  void joinProcess_ThrowsException_WhenPasswordIsEmpty() {
    // given: 비밀번호가 비어 있는 상황
    JoinDTO joinDTO = new JoinDTO("testuser", "", "test@test.com", "nickname");

    // then: IllegalArgumentException이 발생하는지 확인
    assertThrows(IllegalArgumentException.class, () -> joinService.joinProcess(joinDTO));
  }
}