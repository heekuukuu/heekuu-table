package heekuu.news.common.util;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUtil {
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

  public byte[] convertToBytes(MultipartFile file) {
    if (file == null) {
      return null;
    }

    validateImage(file);

    try {
      return file.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("이미지 처리 중 오류가 발생했습니다.", e);
    }
  }

  private void validateImage(MultipartFile file) {
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
    }
  }
}
