package helloworld.studytogether.forbidden.service;

import helloworld.studytogether.common.exception.GlobalExceptionHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ForbiddenService {

    // 금지 대상 단어 리스트 (하드코딩)
    private final List<String> prohibitedWords = Arrays.asList("노잼","내공냠냠");


    // 텍스트에 금지어 포함 여부 확인 후 예외 던짐
    public void validateContent(String content) {
        for (String word : prohibitedWords) {
            if (content.toLowerCase().contains(word.toLowerCase())) {
                throw new ForbiddenWordException("비속어 혹은 금지 단어를 포함하여 등록할 수 없습니다.");
            }
        }
    }

    // Custom Exception: ForbiddenWordException
    public static class ForbiddenWordException extends RuntimeException {
        public ForbiddenWordException(String message) {
            super(message);
        }
    }

}

