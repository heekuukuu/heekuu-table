package helloworld.studytogether.common.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  @ResponseBody
  public ResponseEntity<String> handleCustomException(CustomException ex) {
    return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
  }

  // UsernameNotFoundException 예외 처리
  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody

  public String handleUsernameNotFoundException(UsernameNotFoundException ex) {
    return "아이디를 찾을 수 없습니다.";
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }  // 기타 일반 예외 처리

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    return new ResponseEntity<>("서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
  }

}

