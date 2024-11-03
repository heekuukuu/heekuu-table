package heekuu.news.common.exception;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
  private final LocalDateTime timestamp = LocalDateTime.now();
  private final int status;
  private final String error;
  private final String message;
  private final String path;

  public static ErrorResponse of(heekuu.news.common.exception.ErrorCode errorCode, String path) {
    return ErrorResponse.builder()
        .status(errorCode.getStatus().value())
        .error(errorCode.getStatus().name())
        .message(errorCode.getMessage())
        .path(path)
        .build();
  }
}
