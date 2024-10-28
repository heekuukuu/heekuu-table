package helloworld.studytogether.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter

public enum ErrorCode {

  // 질문 관련
  QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
  INVALID_SUBJECT(HttpStatus.BAD_REQUEST, "잘못된 과목입니다."),


  //일반
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),



  // 유저 관련
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 로그인된 사용자입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
  ALREADY_LOGGED_IN(HttpStatus.CONFLICT, "이미 로그인된 사용자입니다. 로그아웃 후 다시 시도해주세요."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
