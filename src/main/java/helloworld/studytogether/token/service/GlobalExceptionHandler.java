package helloworld.studytogether.token.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // AccessDeniedException 처리 (403 Forbidden)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    // 로그에 예외 메시지 출력 (필요 시 추가적인 정보 로그 가능)
    System.out.println("Access Denied: " + ex.getMessage());

    // 클라이언트에 사용자 친화적인 403 메시지 전송
    return new ResponseEntity<>("접근이 거부되었습니다. 권한이 부족합니다.", HttpStatus.FORBIDDEN);
  }

  // 다른 예외 처리 핸들러 추가 가능
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    // 모든 다른 예외를 처리 (필요 시)
    return new ResponseEntity<>("서버에 오류가 발생했습니다: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}