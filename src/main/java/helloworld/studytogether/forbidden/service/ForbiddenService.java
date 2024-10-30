package helloworld.studytogether.forbidden.service;

import helloworld.studytogether.common.exception.CustomException;
import helloworld.studytogether.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ForbiddenService {

    // 금지 대상 단어 리스트 (하드코딩으로 관리)
    private final List<String> prohibitedWords = Arrays.asList("노잼","존나","ㅅㅂ","tlqkf","ㅈㄴ","ㅗ");


    // 텍스트에 금지어 포함 여부 확인 후 예외 던짐
    public void validateContent(String content) {
        for (String word : prohibitedWords) {
            if (content.toLowerCase().contains(word.toLowerCase())) {
                throw new CustomException(ErrorCode.FORBIDDEN_WORD, HttpStatus.BAD_REQUEST);
            }
        }
    }

}

