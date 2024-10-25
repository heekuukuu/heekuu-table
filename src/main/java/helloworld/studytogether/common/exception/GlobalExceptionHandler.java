package helloworld.studytogether.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // CustomException 처리
  @ExceptionHandler(CustomException.class)
  protected ResponseEntity<ErrorResponse> handleCustomException(
      CustomException e, HttpServletRequest request) {
    log.error("CustomException: {}", e.getMessage());
    ErrorResponse response = ErrorResponse.of(
        e.getErrorCode(),
        request.getRequestURI()
    );
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(response);
  }

  // 유효성 검사 실패 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    log.error("Validation error: {}", e.getMessage());
    ErrorResponse response = ErrorResponse.of(
        ErrorCode.INVALID_INPUT_VALUE,
        request.getRequestURI()
    );
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // IllegalArgumentException 처리
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException e, HttpServletRequest request) {
    log.error("IllegalArgumentException: {}", e.getMessage());
    ErrorResponse response = ErrorResponse.of(
        ErrorCode.INVALID_INPUT_VALUE,
        request.getRequestURI()
    );
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }
}
