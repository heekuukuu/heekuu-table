package helloworld.studytogether.questions.service;

import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.QuestionRequestDto;
import helloworld.studytogether.questions.entity.Question;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
  Question saveQuestion(QuestionRequestDto requestDto) throws IOException;
  Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable);
}
