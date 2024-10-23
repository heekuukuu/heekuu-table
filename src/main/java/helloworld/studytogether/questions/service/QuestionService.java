package helloworld.studytogether.questions.service;

import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.QuestionRequest;
import helloworld.studytogether.questions.entity.Question;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
  Question saveQuestion(QuestionRequest requestDto, Long userId) throws IOException;
  Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable);
}
