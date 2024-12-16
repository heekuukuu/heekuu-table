package heekuu.table.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final HttpStatus status;


  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.status = errorCode.getStatus();
  }


  // CustomException 생성자
  public CustomException(ErrorCode errorCode, HttpStatus status) {
    super(errorCode.getMessage()); // ErrorCode에서 메시지를 가져옴
    this.errorCode = errorCode;
    this.status = status;
  }

  // 필요 시 메시지와 에러 코드를 받는 생성자
  public CustomException(String message, ErrorCode errorCode, HttpStatus status) {
    super(message);
    this.errorCode = errorCode;
    this.status = status;
  }


}
