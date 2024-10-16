package helloworld.studytogether.questions.service;

import java.io.IOException;
import helloworld.studytogether.questions.dto.AddQuestionRequestDto;
import helloworld.studytogether.questions.entity.Question;

public interface QuestionService {
  Question saveQuestion(AddQuestionRequestDto requestDto) throws IOException;
}
