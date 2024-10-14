package questions.service;

import java.io.IOException;
import questions.dto.AddQuestionRequestDto;
import questions.entity.Question;

public interface QuestionService {
  Question saveQuestion(AddQuestionRequestDto requestDto) throws IOException;
}
